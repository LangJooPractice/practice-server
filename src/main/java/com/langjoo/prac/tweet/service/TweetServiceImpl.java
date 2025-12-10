package com.langjoo.prac.tweet.service;

import com.langjoo.prac.common.exception.DuplicateException;
import com.langjoo.prac.domain.Follow;
import com.langjoo.prac.domain.RetweetType;
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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
        // 1. 현재 사용자 조회 (필터링 및 리트윗 여부 확인을 위해 User 객체 필요)
        User currentUser = findUserById(currentUserId);

        // 2. 팔로우하는 모든 사용자 ID 목록 조회
        List<Long> followingUserIds = followRepository.findByFollower(currentUser).stream()
                .map(follow -> follow.getFollowing().getId())
                .collect(Collectors.toList());

        // 3. 자신의 ID도 포함 (자신의 트윗도 피드에 나와야 함)
        followingUserIds.add(currentUserId);

        // 4. 타임라인에 표시할 트윗(팔로우 + 본인)을 DB에서 조회
        List<Tweet> tweets = tweetRepository.findAllByUserIdIn(followingUserIds, pageable);

        // -------------------------------------------------------------
        // 📌 5. 현재 유저의 리트윗 여부 플래그 설정 로직 (통합된 핵심 로직)
        // -------------------------------------------------------------

        // 5-1. 타임라인 트윗 중 원본 트윗 ID 목록을 수집합니다.
        List<Long> originalTargetIds = tweets.stream()
                .map(tweet -> tweet.isRetweet() ? tweet.getOriginalTweet().getId() : tweet.getId())
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // 5-2. 🚨 DB 조회 최소화: 현재 유저가 해당 트윗들을 리트윗했는지 한 번에 조회합니다.
        List<Tweet> userRetweets = tweetRepository.findByUserAndOriginalTweetIdIn(
                currentUser,
                originalTargetIds
        );

        // 5-3. 맵으로 변환: O(1) 시간복잡도로 빠르게 리트윗 여부를 찾기 위함.
        Map<Long, Boolean> retweetedMap = userRetweets.stream()
                .collect(Collectors.toMap(
                        tweet -> tweet.getOriginalTweet().getId(), // Key: 원본 트윗 ID
                        tweet -> true,
                        (existing, replacement) -> existing
                ));

        // -------------------------------------------------------------
// 6. DTO 변환 시 플래그 설정 (수정된 로직)
// -------------------------------------------------------------
        return tweets.stream()
                .map(tweet -> {
                    TweetResponse response = TweetResponse.from(tweet);

                    // 📌 1. 현재 트윗이 인용 트윗(Quote Retweet)인지 확인합니다.
                    boolean isQuoteRetweet = tweet.getRetweetType() == RetweetType.QUOTE_RETWEET;

                    // 2. 트윗이 리트윗인지, 원본 트윗인지에 따라 검사할 최종 원본 ID를 결정합니다.
                    Long targetId = tweet.isRetweet() ? tweet.getOriginalTweet().getId() : tweet.getId();

                    // 3. 📌 isRetweetedByMe 플래그 설정 (조건부 로직)
                    if (isQuoteRetweet) {
                        // 인용 트윗은 '내가 리트윗함'이 아니라 '내가 작성함'이므로 무조건 false
                        response.setRetweetedByMe(false);
                    } else {
                        // 순수 트윗이거나 남의 트윗인 경우에만, Map을 통해 '내가 리트윗했는지' 검사합니다.
                        response.setRetweetedByMe(retweetedMap.containsKey(targetId));
                    }

                    return response;
                })
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------
    // 2. 트윗 상세 조회
    // -------------------------------------------------------------
    @Override
    public TweetResponse getTweetById(Long tweetId) {
        // 2. tweetId로 트윗을 조회
        Tweet tweet = findTweetById(tweetId);

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

    @Override
    public TweetResponse createRetweet(Long userId, Long originalTweetId, String quoteContent) {
        User user = findUserById(userId);
        Tweet originalTweet = findTweetById(originalTweetId);

        // 1. 리트윗 타입 결정: quoteContent의 존재 여부에 따라 PURE 또는 QUOTE 결정
        boolean isPureRetweet = quoteContent == null || quoteContent.trim().isEmpty();
        RetweetType type = isPureRetweet ? RetweetType.PURE_RETWEET : RetweetType.QUOTE_RETWEET;

        // -------------------------------------------------------------
        // 📌 2. 유일성 검증: 순수 리트윗일 경우에만
        // -------------------------------------------------------------
        if (type == RetweetType.PURE_RETWEET) {
            // 💡 [수정]: 기존 findByUserAndOriginalTweet 대신 RetweetType 기반 쿼리 사용
            Optional<Tweet> existingPureRetweet = tweetRepository.findByUserAndOriginalTweetAndRetweetType(
                    user,
                    originalTweet,
                    RetweetType.PURE_RETWEET
            );

            if (existingPureRetweet.isPresent()) {
                throw new DuplicateException("이미 순수 리트윗했습니다. 인용 트윗을 사용해 주세요.");
            }
        }
        // -------------------------------------------------------------

        // 3. 팩토리 메서드를 이용해 리트윗 객체 생성
        // 💡 [수정]: RetweetType을 인자로 넘기도록 수정
        Tweet retweet = Tweet.createRetweet(user, originalTweet, quoteContent, type);

        // 4. 저장
        Tweet savedRetweet = tweetRepository.save(retweet);

        // 5. 원본 트윗의 카운트 증가 (트랜잭션 내에서 처리)
        originalTweet.setRetweetCount(originalTweet.getRetweetCount() + 1);
        tweetRepository.save(originalTweet); // 카운트 업데이트

        // 📌 [수정] DTO 변환 시 isRetweetedByMe 플래그를 true로 설정
        return TweetResponse.from(savedRetweet, true); // 👈 오버로딩된 메서드를 사용합니다.
    }

    // -------------------------------------------------------------
    // 6. 리트윗 취소 (수정 필요)
    // -------------------------------------------------------------
    @Override
    public void cancelRetweet(Long currentUserId, Long originalTweetId) {

        // 1. 순수 리트윗(PURE_RETWEET)을 기준으로 대상 트윗을 조회
        // 💡 [수정]: content="" 대신 RetweetType 기반 쿼리 사용
        Optional<Tweet> retweetToCancel = tweetRepository.findByUserAndOriginalTweetAndRetweetType(
                findUserById(currentUserId),
                findTweetById(originalTweetId),
                RetweetType.PURE_RETWEET // 👈 PURE_RETWEET 타입만 삭제
        );

        if (retweetToCancel.isPresent()) {
            Tweet retweet = retweetToCancel.get();
            // 2. 리트윗 레코드 삭제
            tweetRepository.delete(retweet);

            // 3. 원본 트윗의 RT 카운트 감소 로직
            Tweet originalTweet = findTweetById(originalTweetId);
            originalTweet.setRetweetCount(originalTweet.getRetweetCount() - 1);
            tweetRepository.save(originalTweet); // 카운트 업데이트
        } else {
            // 취소할 리트윗이 없는 경우 (예외 처리)
            throw new NotFoundException("취소할 순수 리트윗을 찾을 수 없거나, 인용 트윗입니다.");
        }
    }
}