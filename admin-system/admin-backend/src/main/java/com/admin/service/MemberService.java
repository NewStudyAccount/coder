package com.admin.service;

import com.admin.entity.Member;
import com.admin.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MemberService {

    @Autowired
    private MemberMapper memberMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<Member> getList(String username, String phone, Integer status) {
        return memberMapper.selectList(username, phone, status);
    }

    public Member getById(Long id) {
        return memberMapper.selectById(id);
    }

    @Transactional
    public void add(Member member) {
        Member existMember = memberMapper.selectByUsername(member.getUsername());
        if (existMember != null) {
            throw new RuntimeException("用户名已存在");
        }
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        member.setStatus(member.getStatus() != null ? member.getStatus() : 1);
        member.setLevel(member.getLevel() != null ? member.getLevel() : 1);
        member.setPoints(member.getPoints() != null ? member.getPoints() : 0);
        member.setBalance(member.getBalance() != null ? member.getBalance() : BigDecimal.ZERO);
        memberMapper.insert(member);
    }

    @Transactional
    public void update(Member member) {
        if (member.getPassword() != null && !member.getPassword().isEmpty()) {
            member.setPassword(passwordEncoder.encode(member.getPassword()));
        } else {
            member.setPassword(null);
        }
        memberMapper.update(member);
    }

    @Transactional
    public void deleteById(Long id) {
        memberMapper.deleteById(id);
    }

    @Transactional
    public void deleteBatch(List<Long> ids) {
        memberMapper.deleteBatch(ids);
    }

    @Transactional
    public void updateBalance(Long id, BigDecimal balance) {
        memberMapper.updateBalance(id, balance);
    }

    @Transactional
    public void updatePoints(Long id, Integer points) {
        memberMapper.updatePoints(id, points);
    }

    @Transactional
    public void resetPassword(Long id, String newPassword) {
        Member member = new Member();
        member.setId(id);
        member.setPassword(passwordEncoder.encode(newPassword));
        memberMapper.update(member);
    }
}
