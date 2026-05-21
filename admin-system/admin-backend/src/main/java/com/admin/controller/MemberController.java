package com.admin.controller;

import com.admin.common.Result;
import com.admin.entity.Member;
import com.admin.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/list")
    public Result<List<Member>> list(@RequestParam(required = false) String username,
                                     @RequestParam(required = false) String phone,
                                     @RequestParam(required = false) Integer status) {
        List<Member> list = memberService.getList(username, phone, status);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Member> getById(@PathVariable Long id) {
        Member member = memberService.getById(id);
        return Result.success(member);
    }

    @PostMapping
    public Result<Void> add(@RequestBody Member member) {
        try {
            memberService.add(member);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping
    public Result<Void> update(@RequestBody Member member) {
        memberService.update(member);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable Long id) {
        memberService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        memberService.deleteBatch(ids);
        return Result.success();
    }

    @PutMapping("/{id}/balance")
    public Result<Void> updateBalance(@PathVariable Long id, @RequestBody Map<String, BigDecimal> body) {
        BigDecimal balance = body.get("balance");
        if (balance == null) {
            return Result.error("余额不能为空");
        }
        memberService.updateBalance(id, balance);
        return Result.success();
    }

    @PutMapping("/{id}/points")
    public Result<Void> updatePoints(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer points = body.get("points");
        if (points == null) {
            return Result.error("积分不能为空");
        }
        memberService.updatePoints(id, points);
        return Result.success();
    }

    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newPassword = body.get("newPassword");
        if (newPassword == null || newPassword.isEmpty()) {
            return Result.error("新密码不能为空");
        }
        memberService.resetPassword(id, newPassword);
        return Result.success();
    }
}
