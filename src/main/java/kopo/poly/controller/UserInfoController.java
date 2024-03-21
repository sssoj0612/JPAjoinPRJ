package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.DateUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Slf4j
@RequestMapping(value = "/user")
@RequiredArgsConstructor
@Controller
public class UserInfoController {

    private final IUserInfoService userInfoService;

    /* 회원가입 화면 이동 */
    @GetMapping(value = "userRegForm")
    public String userRegForm() {
        log.info(this.getClass().getName() + ".user/userRegForm Start!");
        log.info(this.getClass().getName() + ".user/userRegForm End!");
        return "user/userRegForm";
    }

    /* 아이디 중복체크 */
    @ResponseBody
    @PostMapping(value = "getUserIdExists")
    public UserInfoDTO getUserExists(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getUserExists Start!");

        String userId = CmmUtil.nvl(request.getParameter("userId"));

        log.info("userId : " + userId);

        /* Builder 통한 값 저장 */
        UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();

        /* 아이디를 통해 중복 아이디인지 조회 */
        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getUserIdExists(pDTO))
                .orElseGet(() -> UserInfoDTO.builder().build());

        log.info(this.getClass().getName() + ".getUserExists End!");

        return rDTO;

    }

    /* 회원가입 로직 처리 */
    @ResponseBody
    @PostMapping(value = "insertUserInfo")
    public MsgDTO insertUserInfo(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".insertUserInfo Start!");

        String msg; // 회원가입 결과에 대한 메시지를 전달할 변수

        String userId = CmmUtil.nvl(request.getParameter("userId"));
        String userName = CmmUtil.nvl(request.getParameter("userName"));
        String password = CmmUtil.nvl(request.getParameter("password"));
        String email = CmmUtil.nvl(request.getParameter("email"));
        String addr1 = CmmUtil.nvl(request.getParameter("addr1"));
        String addr2 = CmmUtil.nvl(request.getParameter("addr2"));

        log.info("userId : " + userId);
        log.info("userName : " + userName);
        log.info("password : " + password);
        log.info("email : " + email);
        log.info("addr1 : " + addr1);
        log.info("addr2 : " + addr2);

        UserInfoDTO pDTO = UserInfoDTO.builder()
                .userId(userId).userName(userName)
                .password(EncryptUtil.encHashSHA256(password))
                .email(EncryptUtil.encHashSHA256(email))
                .addr1(addr1).addr2(addr2)
                .regId(userId).chgId(userId)
                .build();

        int res = userInfoService.insertUserInfo(pDTO);

        log.info("회원가입 결과(res) : " + res);

        if (res == 1) {
            msg = "회원가입되었습니다.";
        } else if (res == 2) {
            msg = "이미 가입 된 아이디입니다.";
        } else {
            msg = "오류로 인해 회원가입이 실패하였습니다.";
        }

        MsgDTO dto = MsgDTO.builder().result(res).msg(msg).build();

        log.info(this.getClass().getName() + ".insertUserInfo End!");

        return dto;
    }

    /* 로그인 화면으로 이동 */
    @GetMapping(value = "login")
    public String login() {
        log.info(this.getClass().getName() + ".user/login Start!");
        log.info(this.getClass().getName() + ".user/login End!");
        return "user/login";
    }

    /* 로그인 처리 및 결과 알려주는 화면으로 이동 */
    @ResponseBody
    @PostMapping(value = "loginProc")
    public MsgDTO loginProc(HttpServletRequest request, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".loginProc Start!");

        String msg; // 로그인 결과에 대한 메시지를 전달할 변수

        String user_id = CmmUtil.nvl(request.getParameter("user_id"));
        String password = CmmUtil.nvl(request.getParameter("password"));

        log.info("user_id : " + user_id);
        log.info("password : " + password);

        UserInfoDTO pDTO = UserInfoDTO.builder()
                .userId(user_id)
                .password(EncryptUtil.encHashSHA256(password)).build();

        int res = userInfoService.getUserLogin(pDTO);

        log.info("res : " + res);

        if (res == 1) { // 로그인 성공
            msg = "로그인이 성공했습니다.";
            session.setAttribute("SS_USER_ID", user_id);
        } else {
            msg = "아이디와 비밀번호가 올바르지 않습니다.";
        }

        // 결과 메시지 전달하기
        MsgDTO dto = MsgDTO.builder().result(res).msg(msg).build();
        log.info(this.getClass().getName() + ".loginProc End!");

        return dto;
        }

    /* 로그인 성공 화면으로 이동 */
    @GetMapping(value = "loginSuccess")
    public String loginSuccess() {
        log.info(this.getClass().getName() + ".user/loginSuccess Start!");
        log.info(this.getClass().getName() + ".user/loginSuccess End!");
        return "user/loginSuccess";
    }

    /* 로그아웃 처리 */
    @PostMapping(value = "logout")
    public MsgDTO logout(HttpSession session) {
        log.info(this.getClass().getName() + ".logout Start!");

        session.setAttribute("SS_USER_ID","");
        session.removeAttribute("SS_USER_ID");

        MsgDTO dto = MsgDTO.builder().result(1).msg("로그아웃 되었습니다.").build();

        log.info(this.getClass().getName() + ".logout End!");

        return dto;
    }

    }
