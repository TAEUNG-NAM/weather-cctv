package live.narcy.weather.member.repository;

import live.narcy.weather.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByEmail(String email);

    // Optional로 감싸야함(NPE 예방)
    Member findByEmail(String email);
}
