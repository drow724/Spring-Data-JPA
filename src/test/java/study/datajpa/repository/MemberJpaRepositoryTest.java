package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import study.datajpa.entity.Member;

@SpringBootTest
@Transactional
@Rollback(false)
public class MemberJpaRepositoryTest {
	@Autowired
	MemberJpaRepository memberJpaRepository;

	@Test
	public void testMember() {
		Member member = new Member("memberA");
		Member savedMember = memberJpaRepository.save(member);
		Member findMember = memberJpaRepository.find(savedMember.getId());
		assertThat(findMember.getId()).isEqualTo(member.getId());
		assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
		assertThat(findMember).isEqualTo(member); // JPA 엔티티 동일성 보장
	}

	@Test
	public void basicCRUD() {
		Member member1 = new Member("member1");
		Member member2 = new Member("member2");
		memberJpaRepository.save(member1);
		memberJpaRepository.save(member2);
		// 단건 조회 검증
		Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
		Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
		assertThat(findMember1).isEqualTo(member1);
		assertThat(findMember2).isEqualTo(member2);
		// 리스트 조회 검증
		List<Member> all = memberJpaRepository.findAll();
		assertThat(all.size()).isEqualTo(2);
		// 카운트 검증
		long count = memberJpaRepository.count();
		assertThat(count).isEqualTo(2);

		// 변경 감지 (굳이 update문을 쓸 필요 없다)
		// findMember1.setUsername("member!!!");

		// 삭제 검증
		memberJpaRepository.delete(member1);
		memberJpaRepository.delete(member2);
		long deletedCount = memberJpaRepository.count();
		assertThat(deletedCount).isEqualTo(0);
	}

	@Test
	public void findByUsernameAndAgeGreaterThan() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("AAA", 20);
		memberJpaRepository.save(m1);
		memberJpaRepository.save(m2);
		List<Member> result = memberJpaRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
		assertThat(result.get(0).getUsername()).isEqualTo("AAA");
		assertThat(result.get(0).getAge()).isEqualTo(20);
		assertThat(result.size()).isEqualTo(1);
	}

}