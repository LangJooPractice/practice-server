package com.langjoo.prac.follow.repository;

import com.langjoo.prac.domain.Follow;
import com.langjoo.prac.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    // 특정 팔로워가 특정 팔로잉을 하고 있는지 확인 (팔로우 상태 확인)
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    // 특정 사용자를 팔로우하는 모든 관계 목록 조회 (팔로워 목록)
    List<Follow> findByFollowing(User following);

    // 특정 사용자가 팔로우하는 모든 관계 목록 조회 (팔로잉 목록)
    List<Follow> findByFollower(User follower);

    // 팔로우 관계가 존재하는지 여부를 빠르게 확인
    boolean existsByFollowerAndFollowing(User follower, User following);

    // 📌 2. 팔로잉 수 계산 (현재 User가 'Follower'인 관계의 수)
    // SELECT COUNT(f) FROM Follow f WHERE f.follower = :profileUser
    long countByFollower(User profileUser);

    // 📌 3. 팔로워 수 계산 (현재 User가 'Following'인 관계의 수)
    // SELECT COUNT(f) FROM Follow f WHERE f.following = :profileUser
    long countByFollowing(User profileUser);

}
