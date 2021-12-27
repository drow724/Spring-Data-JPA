package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	TeamRepository teamRepository;

	@PersistenceContext
	EntityManager em;

	@Test
	public void testMember() {
		Member member = new Member("memberA");
		Member savedMember = memberRepository.save(member);
		Member findMember = memberRepository.findById(savedMember.getId()).get();
		Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());

		Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
		Assertions.assertThat(findMember).isEqualTo(member); // JPA 엔티티 동일성 보장
	}

	@Test
	public void basicCRUD() {
		Member member1 = new Member("member1");
		Member member2 = new Member("member2");
		memberRepository.save(member1);
		memberRepository.save(member2);
		// 단건 조회 검증
		Member findMember1 = memberRepository.findById(member1.getId()).get();
		Member findMember2 = memberRepository.findById(member2.getId()).get();
		assertThat(findMember1).isEqualTo(member1);
		assertThat(findMember2).isEqualTo(member2);
		// 리스트 조회 검증
		List<Member> all = memberRepository.findAll();
		assertThat(all.size()).isEqualTo(2);
		// 카운트 검증
		long count = memberRepository.count();
		assertThat(count).isEqualTo(2);
		// 삭제 검증
		memberRepository.delete(member1);
		memberRepository.delete(member2);
		long deletedCount = memberRepository.count();
		assertThat(deletedCount).isEqualTo(0);
	}

	@Test
	public void findByUsernameAndAgeGreaterThan() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("AAA", 20);
		memberRepository.save(m1);
		memberRepository.save(m2);
		List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
		assertThat(result.get(0).getUsername()).isEqualTo("AAA");
		assertThat(result.get(0).getAge()).isEqualTo(20);
		assertThat(result.size()).isEqualTo(1);
	}

	@Test
	public void testNamedQuery() {

		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);

		memberRepository.save(m1);
		memberRepository.save(m2);

		List<Member> result = memberRepository.findByUsername("AAA");

		Member findMember = result.get(0);

		assertThat(findMember).isEqualTo(m1);

	}

	@Test
	public void testQuery() {

		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);

		memberRepository.save(m1);
		memberRepository.save(m2);

		List<Member> result = memberRepository.findUser("AAA", 10);

		assertThat(result.get(0)).isEqualTo(m1);

	}

	@Test
	public void findUsernameList() {

		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);

		memberRepository.save(m1);
		memberRepository.save(m2);

		List<String> usernameList = memberRepository.findUsernameList();

		// 실제 실무에서 테스트할때는 assertThat사용
		for (String s : usernameList) {
			System.out.println(s);
		}

	}

	@Test
	public void findMemberDto() {

		Team team = new Team("teamA");
		teamRepository.save(team);

		Member m1 = new Member("AAA", 10);
		m1.setTeam(team);
		memberRepository.save(m1);

		List<MemberDto> memberDto = memberRepository.findMemberDto();

		// 실제 실무에서 테스트할때는 assertThat사용
		for (MemberDto dto : memberDto) {
			System.out.println("dto = " + dto);
		}

	}

	@Test
	public void findByNames() {

		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);

		memberRepository.save(m1);
		memberRepository.save(m2);

		List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

		// 실제 실무에서 테스트할때는 assertThat사용
		for (Member member : result) {
			System.out.println("member = " + member);
		}

	}

	@Test
	public void returnType() {

		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);

		memberRepository.save(m1);
		memberRepository.save(m2);

		List<Member> aaa = memberRepository.findListByUsername("AAA");

		// 실제 실무에서 테스트할때는 assertThat사용
		for (Member member : aaa) {
			System.out.println("member = " + member);
		}

		Member findMember = memberRepository.findMemberByUsername("AAA");
		System.out.println("findMember = " + findMember);

		Optional<Member> optionalMember = memberRepository.findOptionalByUsername("AAA");
		System.out.println("optionalMember = " + optionalMember.get());

		// List<Member> result = memberRepository.findListByUsername("ffgdfgfdsg");

		// 콜렉션이 무조건 반환됨으로 안좋은 코드이다.
		// if(result != null) {
		// System.out.println("result = " + result.size());
		// }

		// System.out.println("result = " + result.size());

		// Member result = memberRepository.findMemberByUsername("ㄻㄴㄹㅇㄴㅁㄹㅇㄴ");
		// null이 나온다
		// System.out.println("findMember = " + result);

		Optional<Member> result = memberRepository.findOptionalByUsername("AAA");
		System.out.println("findMember = " + result);
	}

	@Test
	public void paging() throws Exception {
		// given
		memberRepository.save(new Member("member1", 10));
		memberRepository.save(new Member("member2", 10));
		memberRepository.save(new Member("member3", 10));
		memberRepository.save(new Member("member4", 10));
		memberRepository.save(new Member("member5", 10));

		int age = 10;
		PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

		// when
		Page<Member> page = memberRepository.findByAge(age, pageRequest);

		Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
		// then
		List<Member> content = page.getContent();
		long totalElements = page.getTotalElements();

		assertThat(content.size()).isEqualTo(3);
		assertThat(page.getTotalElements()).isEqualTo(5);
		assertThat(page.getNumber()).isEqualTo(0);
		assertThat(page.getTotalPages()).isEqualTo(2);
		assertThat(page.isFirst()).isTrue();
		assertThat(page.hasNext()).isTrue();
	}

	@Test
	public void bulkUpdate() throws Exception {
		// given
		memberRepository.save(new Member("member1", 10));
		memberRepository.save(new Member("member2", 19));
		memberRepository.save(new Member("member3", 20));
		memberRepository.save(new Member("member4", 21));
		memberRepository.save(new Member("member5", 40));

		// when
		// 영속성 컨텍스트를 무시하고 bulk연산을 해버림
		int resultCount = memberRepository.bulkAgePlus(20);

		// age는 41살이 아닌 40살이 나온다.
//		List<Member> result = memberRepository.findByUsername("member5");
//		Member member5 = result.get(0);
//		System.out.println("member5 = " + member5);

//		em.flush();
//		em.clear();

		List<Member> result = memberRepository.findByUsername("member5");
		Member member5 = result.get(0);
		System.out.println("member5 = " + member5);

		// then
		assertThat(resultCount).isEqualTo(3);
	}

	@Test
	public void findMemberLazy() throws Exception {
		// given
		// member1 -> teamA
		// member2 -> teamB
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		teamRepository.save(teamA);
		teamRepository.save(teamB);
		memberRepository.save(new Member("member1", 10, teamA));
		memberRepository.save(new Member("member1", 20, teamB));
		em.flush();
		em.clear();

		// when N + 1
		// select Member 1
		// List<Member> members = memberRepository.findMemberFetchJoin();
		// List<Member> members = memberRepository.findAll();
		// List<Member> members = memberRepository.findMemberEntityGraph();
		List<Member> members = memberRepository.findEntityGraphByUsername("member1");

		// then
		for (Member member : members) {
			// select Team from Team t where = ?
			member.getTeam().getName();
		}
	}

	@Test
	public void queryHint() throws Exception {
		// given
		memberRepository.save(new Member("member1", 10));
		em.flush();
		em.clear();

		// when
		Member member = memberRepository.findReadOnlyByUsername("member1");
		member.setUsername("member2");

		em.flush(); // Update Query 실행X
	}

	@Test
	public void lock() {
		// given
		memberRepository.save(new Member("member1", 10));
		em.flush();
		em.clear();

		// when
		List<Member> result = memberRepository.findLockByUsername("member1");

	}

	@Test
	public void callCustom() {
		// given
		List<Member> result = memberRepository.findMemberCustom();
	}

	@Test
	public void specBasic() throws Exception {
		// given
		Team teamA = new Team("teamA");
		em.persist(teamA);
		Member m1 = new Member("m1", 0, teamA);
		Member m2 = new Member("m2", 0, teamA);
		em.persist(m1);
		em.persist(m2);
		em.flush();
		em.clear();
		// when
		Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
		List<Member> result = memberRepository.findAll(spec);
		// then
		Assertions.assertThat(result.size()).isEqualTo(1);
	}

	@Test
	public void queryByExample() {

		// given
		Team teamA = new Team("teamA");
		em.persist(teamA);
		Member m1 = new Member("m1", 0, teamA);
		Member m2 = new Member("m2", 0, teamA);
		em.persist(m1);
		em.persist(m2);
		em.flush();
		em.clear();

		// when
		// probe
		Member member = new Member("m1");
		Team team = new Team("teamA");
		member.setTeam(team);

		ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");

		Example<Member> example = Example.of(member, matcher);

		List<Member> result = memberRepository.findAll(example);

		// then
		Assertions.assertThat(result.get(0).getUsername()).isEqualTo("m1");
	}

	@Test
	public void projections() throws Exception {
		// given
		Team teamA = new Team("teamA");
		em.persist(teamA);
		Member m1 = new Member("m1", 0, teamA);
		Member m2 = new Member("m2", 0, teamA);
		em.persist(m1);
		em.persist(m2);
		em.flush();
		em.clear();
		// when
		List<NestedClosedProjection> result = memberRepository.findProjectionsByUsername("m1",NestedClosedProjection.class);
		// then
		Assertions.assertThat(result.size()).isEqualTo(1);
	}
}