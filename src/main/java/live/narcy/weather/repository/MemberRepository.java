package live.narcy.weather.repository;

import live.narcy.weather.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByEmail(String email);

    // Optional로 감싸야함(NPE 예방)
    Member findByEmail(String email);
}
