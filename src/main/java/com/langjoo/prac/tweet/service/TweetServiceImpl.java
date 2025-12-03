package com.langjoo.prac.tweet.service;

import com.langjoo.prac.domain.Follow;
import com.langjoo.prac.domain.Tweet;
import com.langjoo.prac.domain.User;
import com.langjoo.prac.follow.repository.FollowRepository;
import com.langjoo.prac.tweet.dto.TweetRequest;
import com.langjoo.prac.tweet.dto.TweetResponse;
import com.langjoo.prac.tweet.repository.TweetRepository;
import com.langjoo.prac.user.repository.UserRepository;
import jakarta.transaction.Transactional; // 트랜잭션 관리를 위해 사용
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// 사용자 정의 예외 클래스가 있다고 가정 (예: NotFoundException, UnauthorizedException)
import com.langjoo.prac.common.exception.NotFoundException;
import com.langjoo.prac.common.exception.UnauthorizedException;

@Service
@RequiredArgsConstructor
@Transactional
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository; // 피드 생성을 위해 필요

    // 유틸리티 메서드: User 객체를 찾는 메서드
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
    }

    // 유틸리티 메서드: Tweet 객체를 찾는 메서드
    private Tweet findTweetById(Long tweetId) {
        return tweetRepository.findById(tweetId)
                .orElseThrow(() -> new NotFoundException("트윗을 찾을 수 없습니다."));
    }

    // -------------------------------------------------------------
    // 1. 팔로우 트윗 피드 조회 (홈 화면)
    // -------------------------------------------------------------
    @Override
    public List<TweetResponse> getTimelineFeed(Long currentUserId, Pageable pageable) {
        // 1. 현재 사용자 조회
        User currentUser = findUserById(currentUserId);

        // 2. 팔로우하는 모든 사용자 ID 목록 조회
        List<Long> followingUserIds = followRepository.findByFollower(currentUser).stream()
                .map(follow -> follow.getFollowing().getId())
                .collect(Collectors.toList());

        // 3. 자신의 ID도 포함 (자신의 트윗도 피드에 나와야 함)
        followingUserIds.add(currentUserId);

        // 4. 해당 ID들이 작성한 모든 트윗을 페이지네이션 및 최신순으로 조회 (Repository에 쿼리 메서드 필요)
        // (주의: JpaRepository에는 List<Long>을 받는 findByUserIds In 쿼리를 직접 작성해야 합니다.)

        // 임시 로직: 트윗 ID 목록이 준비되었다고 가정하고 findAll() 대체
        List<Tweet> tweets = tweetRepository.findAll(pageable).getContent();

        return tweets.stream()
                .map(TweetResponse::from) // DTO로 변환
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------
    // 2. 트윗 상세 조회
    // -------------------------------------------------------------
    @Override
    public TweetResponse getTweetByUsernameAndId(String username, Long tweetId) {
        // 1. username으로 User를 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("작성자(@" + username + ")를 찾을 수 없습니다."));

        // 2. tweetId로 트윗을 조회
        Tweet tweet = findTweetById(tweetId);

        // 3. (선택적) URL의 username과 트윗의 실제 작성자 일치 여부 검증
        if (!tweet.getUser().getId().equals(user.getId())) {
            // URL 경로에 잘못된 username이 포함된 경우
            throw new NotFoundException("해당 작성자의 트윗이 아니거나 트윗을 찾을 수 없습니다.");
        }

        // 4. DTO로 변환하여 반환
        return TweetResponse.from(tweet);
    }

    // -------------------------------------------------------------
    // 3. 신규 트윗 작성
    // -------------------------------------------------------------
    @Override
    public TweetResponse createTweet(Long userId, TweetRequest request) {
        User user = findUserById(userId);

        // content validation은 Controller의 @Valid에서 1차로 처리됨
        Tweet tweet = new Tweet(user, request.getContent());

        Tweet savedTweet = tweetRepository.save(tweet);
        return TweetResponse.from(savedTweet);
    }

    // -------------------------------------------------------------
    // 4. 트윗 삭제
    // -------------------------------------------------------------
    @Override
    public void deleteTweet(Long userId, Long tweetId) {
        Tweet tweet = findTweetById(tweetId);

        // 1. 권한 검증: 트윗 작성자와 현재 사용자가 일치하는지 확인
        if (!tweet.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("해당 트윗을 삭제할 권한이 없습니다.");
        }

        // 2. 삭제
        tweetRepository.delete(tweet);

        // 📌 참고: 좋아요, 리트윗 카운트 감소 로직 (별도 Like/Retweet Service에서 처리될 수 있음)
    }

    // -------------------------------------------------------------
    // 5. 리트윗 또는 인용 트윗 생성
    // -------------------------------------------------------------
    @Override
    public TweetResponse createRetweet(Long userId, Long originalTweetId, String quoteContent) {
        User user = findUserById(userId);
        Tweet originalTweet = findTweetById(originalTweetId);

        // 1. 이미 리트윗 했는지 확인 (선택적)
        // List<Tweet> existingRetweets = tweetRepository.findByUserAndOriginalTweet(user, originalTweet);
        // if (!existingRetweets.isEmpty()) { throw new AlreadyRetweetedException("이미 리트윗 했습니다."); }

        // 2. 팩토리 메서드를 이용해 리트윗 객체 생성
        Tweet retweet = Tweet.createRetweet(user, originalTweet, quoteContent);

        // 3. 저장
        Tweet savedRetweet = tweetRepository.save(retweet);

        // 4. 원본 트윗의 카운트 증가 (트랜잭션 내에서 처리)
        originalTweet.setRetweetCount(originalTweet.getRetweetCount() + 1);
        tweetRepository.save(originalTweet); // 카운트 업데이트

        return TweetResponse.from(savedRetweet);
    }

    // -------------------------------------------------------------
    // 6. 리트윗 취소
    // -------------------------------------------------------------
    @Override
    public void cancelRetweet(Long userId, Long originalTweetId) {
        User user = findUserById(userId);
        Tweet originalTweet = findTweetById(originalTweetId);

        // 1. 해당 사용자가 원본 트윗을 리트윗한 Retweet 엔티티를 찾음
        // (이 로직은 TweetRepository에 쿼리 메서드가 필요)
        Tweet retweet = tweetRepository.findByUserAndOriginalTweet(user, originalTweet)
                .orElseThrow(() -> new NotFoundException("취소할 리트윗을 찾을 수 없습니다."));

        // 2. 삭제
        tweetRepository.delete(retweet);

        // 3. 원본 트윗의 카운트 감소 (트랜잭션 내에서 처리)
        originalTweet.setRetweetCount(originalTweet.getRetweetCount() - 1);
        tweetRepository.save(originalTweet); // 카운트 업데이트
    }
}