package kopo.poly.service;

import kopo.poly.dto.UserInfoDTO;

public interface IUserInfoService {

    /* 아이디 중복 체크 */
    UserInfoDTO getUserIdExists(UserInfoDTO pDTO) throws Exception;

    /* 회원 가입 */
    int insertUserInfo(UserInfoDTO pDTO) throws Exception;

    /* 로그인을 위해 아이디와 비번이 일치하는지 확인 */
    int getUserLogin(UserInfoDTO pDTO) throws Exception;
}
