package kopo.poly.service.ipml;

import kopo.poly.dto.UserInfoDTO;
import kopo.poly.repository.UserInfoRepository;
import kopo.poly.repository.entity.UserInfoEntity;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service("UserInfoService")
public class UserInfoService implements IUserInfoService {

    /* RequiredArgsConstructor 어노테이션으로 생성자를 자동 생성
    * userInfoRepository 변수에 이미 메모리에 올라간 UserInfoRepository 객체를 넣어줌
    * 예전에는 autowired 어노테이션을 통해 설정했지만 이젠 생성자를 통해 객체 주입함 */
    private final UserInfoRepository userInfoRepository;


    /* 아이디 중복체크 */
    @Override
    public UserInfoDTO getUserIdExists(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getUserIdExists Start!");

        UserInfoDTO rDTO;

        String userId = CmmUtil.nvl(pDTO.userId()); // 아이디

        log.info("userId : " + userId);

        /* 회원가입 중복 방지를 위해 DB에서 데이터 조회 */
        Optional<UserInfoEntity> rEntity = userInfoRepository.findByUserId(userId);

        /* 값이 존재하면 (이미 회원가입 된 아이디) */
        if (rEntity.isPresent()) {
            rDTO = UserInfoDTO.builder().existsYn("Y").build();
        } else {
            rDTO = UserInfoDTO.builder().existsYn("N").build();
        }

        log.info(this.getClass().getName() + ".getUserIdExists End!");

        return rDTO;
    }



    /* 회원가입 */

    @Override
    public int insertUserInfo(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertUserInfo Start!");

        /* 회원가입 성공 : 1, 아이디 중복으로 가입 취소 : 2, 기타 에러 : 0 */
        int res = 0;

        String userId = CmmUtil.nvl(pDTO.userId());
        String userName = CmmUtil.nvl(pDTO.userName());
        String password = CmmUtil.nvl(pDTO.password());
        String email = CmmUtil.nvl(pDTO.email());
        String addr1 = CmmUtil.nvl(pDTO.addr1());
        String addr2 = CmmUtil.nvl(pDTO.addr2());

        log.info("pDTO : " + pDTO); // 컨트롤러에서 값 전달 잘 되었는지 확인

        /* 회원가입 중복 방지를 위해 DB에서 데이터 조회 */
        Optional<UserInfoEntity> rEntity = userInfoRepository.findByUserId(userId);

        /* 값이 존재하면 (이미 회원가입 된 아이디) */
        if (rEntity.isPresent()) {
            res = 2;
        } else {

            /* 회원가입을 위한 Entity 생성 */
            UserInfoEntity pEntity = UserInfoEntity.builder()
                    .userId(userId).userName(userName)
                    .password(password)
                    .email(email)
                    .addr1(addr1).addr2(addr2)
                    .regId(userId).regDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                    .chgId(userId).chgDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                    .build();

            /* 회원정보 DB에 저장 */
            userInfoRepository.save(pEntity);

            /* JPA의 save 함수는 데이터 값에 따라 등록, 수정을 수행
             * 물론 잘 저장되겠지만 내가 실행한 save 함수가 DB에 등록이 잘 수행되었는지 100% 확신이 불가능
             * 회원가입 후 , 혹시 저장 안될 수 있기에 조회 수행함
             * 회원가입 중복 방지를 위해 DB에서 데이터 조회
             * */
            rEntity = userInfoRepository.findByUserId(userId);

            /* 값이 존재한다면 (회원가입 성공) */
            if (rEntity.isPresent()) {
                res = 1;
            } else { /* 값이 없다면 (회원가입 실패) */
                res = 0;
            }
        }

        log.info(this.getClass().getName() + ".insertUserInfo End!");

            return res;
        }




    /* 로그인을 위해 아이디와 비번 일치하는지 확인 */
    @Override
    public int getUserLogin(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ". getUserLogin Start!");

        /* 로그인 성공 : 1, 실패 : 0 */
        int res = 0;

        String userId = CmmUtil.nvl(pDTO.userId());
        String password = CmmUtil.nvl(pDTO.password());

        log.info("userId : " + userId);
        log.info("password : " + password);

        /* 로그인을 위해 아이디와 비번이 일치하는지 확인하기 위한 JPA 호출 */
        Optional<UserInfoEntity> rEntity = userInfoRepository.findByUserIdAndPassword(userId, password);

        if (rEntity.isPresent()) {
            res = 1;
        }

        log.info(this.getClass().getName() + ". getUserLoginCheck End!");

        return res;
    }
}
