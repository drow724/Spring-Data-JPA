package study.datajpa.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.sun.source.tree.MemberReferenceTree;

import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom{

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

	// Page<Member> findByAge(int age, Pageable pageable); // count 쿼리 사용

	// Slice<Member> findByAge(String name, Pageable pageable); // count 쿼리 사용안함

	List<Member> findByAge(String name, Pageable pageable); // count 쿼리 사용 안함

	List<Member> findByAge(String name, Sort sort);

	// join할 경우 자동으로 count도 복잡하게 보내기 때문에 count용 쿼리를 단순하게 분리 할 수 있다.
	@Query(value = "select m from Member m left join m.team t", countQuery = "select count(m.username) from Member m")
	Page<Member> findByAge(int age, Pageable pageable); // count 쿼리 사용

	@Modifying(clearAutomatically = true)
	@Query("update Member m set m.age = m.age + 1 where m.age >= :age")
	int bulkAgePlus(@Param("age") int age);

	@Query("select m from Member m left join fetch m.team")
	List<Member> findMemberFetchJoin();

	// 공통 메서드 오버라이드
	@Override
	@EntityGraph(attributePaths = { "team" })
	List<Member> findAll();

	// JPQL + 엔티티 그래프
	@EntityGraph(attributePaths = { "team" })
	@Query("select m from Member m")
	List<Member> findMemberEntityGraph();

	// 메서드 이름으로 쿼리에서 특히 편리하다.
	@EntityGraph(attributePaths = { "team" })
	List<Member> findEntityGraphByUsername(String username);

//	@EntityGraph("Member.all")
//	@Query("select m from Member m")
//	List<Member> findMemberEntityGraph();

	@QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
	Member findReadOnlyByUsername(String username);

	@QueryHints(value = { @QueryHint(name = "org.hibernate.readOnly", value = "true") }, forCounting = true)
	Page<Member> findByUsername(String name, Pageable pageable);
	
	//실시간 트래픽이 많은 서비스에서는 lock 금지
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	List<Member> findLockByUsername(String name);
}
