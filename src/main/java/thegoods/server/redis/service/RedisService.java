package thegoods.server.redis.service;


import thegoods.server.member.presentation.dto.MemberRequestDTO;
import thegoods.server.redis.domain.RefreshToken;

public interface RedisService {

    // 로그인시 accessToken과 같이 발급
    RefreshToken generateRefreshToken(String email);

    // accessToken 만료 시 발급 혹은 그대로 반환
    RefreshToken reGenerateRefreshToken(MemberRequestDTO.RefreshTokenDTO request);

    // 로그아웃을 위해 로그인 되었다고 저장
    String saveLoginStatus(Long memberId, String accessToken);

    // 로그아웃 처리
    void resolveLogout(String accessToken);

    void deleteRefreshToken(String refreshToken);

    // jwt 검증 후 로그아웃 하고 버리지 않고 다시 쓰려는 토큰인지 체크
    Boolean validateLoginToken(String accessToken);
}
