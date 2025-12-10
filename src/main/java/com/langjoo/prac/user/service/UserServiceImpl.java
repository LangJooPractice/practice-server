package com.langjoo.prac.user.service;

import com.langjoo.prac.domain.Tweet;
import com.langjoo.prac.domain.User;
import com.langjoo.prac.follow.repository.FollowRepository;
import com.langjoo.prac.tweet.dto.TweetResponse;
import com.langjoo.prac.tweet.repository.TweetRepository;
import com.langjoo.prac.user.dto.UserRegisterRequest;
import com.langjoo.prac.user.dto.UserProfileResponse;
import com.langjoo.prac.user.dto.UserUpdateRequest;
import com.langjoo.prac.user.repository.UserRepository;
import com.langjoo.prac.common.exception.DuplicateException;
import com.langjoo.prac.common.exception.NotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // 비밀번호 암호화
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Spring Security 설정 시 주입
    private final TweetRepository tweetRepository;
    private final FollowRepository followRepository;

    // -------------------------------------------------------------
    // 1. 신규 회원 가입
    // -------------------------------------------------------------
    @Override
    public void registerUser(UserRegisterRequest request) {

        // 1. loginId 중복 확인
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new DuplicateException("이미 존재하는 로그인 ID입니다.");
        }

        // 2. username 중복 확인 (트위터의 @아이디는 고유해야 함)
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateException("이미 사용 중인 사용자 이름(@아이디)입니다.");
        }

        // 3. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 4. User 엔티티 생성
        User newUser = new User(
                request.getLoginId(),
                request.getUsername(),
                request.getNickname(),
                encodedPassword // 암호화된 비밀번호 저장
        );

        // 5. 저장
        userRepository.save(newUser);
    }

    // -------------------------------------------------------------
    // 2. 특정 사용자의 프로필 조회
    // -------------------------------------------------------------
    @Override
    @Transactional // 읽기 전용 트랜잭션으로 최적화 (데이터 변경이 없으므로)
    public UserProfileResponse getUserProfile(Long currentUserId, String username) {

        // 1. username으로 사용자 조회 (NotFoundException 발생 가능)
        User profileUser = userRepository.findByUsername( username)
                .orElseThrow(() -> new NotFoundException("사용자 @" + username + "을(를) 찾을 수 없습니다."));

        // 2. 현재 로그인 사용자 (A) 조회
        User currentUser = findUserById(currentUserId);

        // 2. 통계 정보 조회 및 계산
        // 좋아요/리트윗 카운트까지 통합하여 UserProfileResponse를 구성

        // 작성한 총 트윗 수
        long tweetCount = tweetRepository.countByUser(profileUser);

        // 팔로잉 수 (User가 Follower인 관계의 수)
        long followingCount = followRepository.countByFollower(profileUser);

        // 팔로워 수 (User가 Following인 관계의 수)
        long followerCount = followRepository.countByFollowing(profileUser);

        /// 3. 📌 [추가] 팔로우 여부 확인 로직
        // 현재 로그인 유저(Follower)가 프로필 대상 유저(Following)를 팔로우하는지 확인
        boolean isFollowing = followRepository.findByFollowerAndFollowing(
                currentUser,
                profileUser
        ).isPresent();

        // 3. 최신 트윗 목록 조회 (페이지네이션 없이 20개만 조회한다고 가정)
        // 트윗을 가져올 때 User 엔티티가 필요합니다.
        List<TweetResponse> recentTweets = tweetRepository.findTop20ByUserOrderByCreatedAtDesc(profileUser).stream()
                .map(TweetResponse::from)
                .collect(Collectors.toList());

        // 4. DTO로 변환하여 반환
        return UserProfileResponse.from(
                profileUser,
                tweetCount,
                followingCount,
                followerCount,
                isFollowing,
                recentTweets
        );
    }

    // -------------------------------------------------------------
    // 3. 회원 탈퇴 (비활성화)
    // -------------------------------------------------------------
    @Override
    public void deactivateUser(Long userId) {
        // 1. 사용자 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        // 2. 실제 트위터는 계정을 삭제하지 않고 '비활성화' 상태로 30일간 유지합니다.
        // 여기서는 간단하게 DB에서 삭제 처리 또는 'is_active' 플래그를 false로 변경합니다.

        // 예시: 삭제 처리 (주의: cascade 설정에 따라 관련 데이터가 모두 삭제됨)
        userRepository.delete(user);

        // 💡 실제 서비스에서는 user.setActive(false)와 같이 상태를 변경하는 것이 좋습니다.
    }

    @Override
    public UserProfileResponse updateUser(Long currentUserId, UserUpdateRequest request) {
        // 1. 현재 사용자 엔티티 조회 및 업데이트
        User user = findUserById(currentUserId);

        // 2. 유저네임 변경 및 중복 검증 (기존 로직 유지)
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                throw new DuplicateException("이미 사용 중인 유저네임입니다: " + request.getUsername());
            }
            user.setUsername(request.getUsername());
        }

        // 3. 나머지 필드 업데이트 (기존 로직 유지)
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }

        // -------------------------------------------------------------
        // 📌 4. [추가] 프로필 타임라인 로딩 값 (집계 정보) 계산
        // -------------------------------------------------------------

        // 이 로직은 getUserProfile 메서드에서 가져와야 합니다.
        // 4-1. 트윗 카운트 계산
        long tweetCount = tweetRepository.countByUser(user);

        // 4-2. 팔로잉 카운트 계산 (user가 Follower)
        long followingCount = followRepository.countByFollower(user);

        // 4-3. 팔로워 카운트 계산 (user가 Following)
        long followerCount = followRepository.countByFollowing(user);

        // 4-4. [중요] '내가 나를 팔로우 하는가' 플래그는 항상 false여야 함
        //      (만약 이 메서드가 프로필 주인의 정보를 반환하는 것이라면, isFollowing은 이 로직에서 필요 없음)
        //      하지만 DTO 구조상 필요하므로, 여기서는 기본값인 false를 가정합니다.
        boolean isFollowing = false; // 프로필 주인의 관점에서는 의미가 없으므로 false

        // 4-5. 최근 트윗 목록 조회 (필요하다면 로직 추가)
        List<Tweet> recentTweets = tweetRepository.findTop20ByUserOrderByCreatedAtDesc(user);
        List<TweetResponse> recentTweetResponses = recentTweets.stream()
                .map(TweetResponse::from)
                .collect(Collectors.toList());

        // -------------------------------------------------------------
        // 5. [수정] 모든 집계 정보를 포함한 긴 팩토리 메서드 호출
        // -------------------------------------------------------------
        return UserProfileResponse.from(
                user,
                tweetCount,
                followingCount,
                followerCount,
                isFollowing,
                recentTweetResponses
        );
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다: ID " + userId));
    }
}