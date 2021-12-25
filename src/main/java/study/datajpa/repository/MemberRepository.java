package study.datajpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import study.datajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	// @Query(name = "Member.findByUsername")
	List<Member> findByUsername(@Param("username") String username);

	List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

	//애플리케이션 로딩 시점에 미리 스프링이 parsing해 보기 때문에 실행 되지 않는 장점이 있다.
	@Query("select m from Member m where m.username= :username and m.age = :age")
	List<Member> findUser(@Param("username") String username, @Param("age") int age);

}
