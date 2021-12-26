package study.datajpa.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	// @Query(name = "Member.findByUsername")
	List<Member> findByUsername(@Param("username") String username);

	List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

	// 애플리케이션 로딩 시점에 미리 스프링이 parsing해 보기 때문에 실행 되지 않는 장점이 있다.
	@Query("select m from Member m where m.username= :username and m.age = :age")
	List<Member> findUser(@Param("username") String username, @Param("age") int age);

	@Query("select m.username from Member m")
	List<String> findUsernameList();

	@Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) " + "from Member m join m.team t")
	List<MemberDto> findMemberDto();

	@Query("select m from Member m where m.username = :name")
	Member findMembers(@Param("name") String username);

	@Query("select m from Member m where m.username in :names")
	List<Member> findByNames(@Param("names") Collection<String> names);

	List<Member> findListByUsername(String name); // 컬렉션

	Member findMemberByUsername(String name); // 단건

	Optional<Member> findOptionalByUsername(String name); // 단건 Optional

	//Page<Member> findByAge(int age, Pageable pageable); // count 쿼리 사용

	//Slice<Member> findByAge(String name, Pageable pageable); // count 쿼리 사용안함

	List<Member> findByAge(String name, Pageable pageable); // count 쿼리 사용 안함

	List<Member> findByAge(String name, Sort sort);
	
	//join할 경우 자동으로 count도 복잡하게 보내기 때문에 count용 쿼리를 단순하게 분리 할 수 있다.
	@Query(value = "select m from Member m left join m.team t"
			,countQuery = "select count(m.username) from Member m")
	Page<Member> findByAge(int age, Pageable pageable); // count 쿼리 사용
	
	@Modifying(clearAutomatically = true)
	@Query("update Member m set m.age = m.age + 1 where m.age >= :age")
	int bulkAgePlus(@Param("age") int age);
}
