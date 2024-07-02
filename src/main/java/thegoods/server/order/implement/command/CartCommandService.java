package thegoods.server.order.implement.command;

import thegoods.server.member.domain.Member;
import thegoods.server.order.presentation.dto.CartRequestDTO;

public interface CartCommandService {
    void addCart(CartRequestDTO.cartAddDTOList request, Member member);

    void updateCart(CartRequestDTO.cartUpdateDTOList request, Member member);

    void deleteCart(CartRequestDTO.cartOptionDeleteDTO request, Member member);

    void deleteCartByItem(CartRequestDTO.cartDeleteByItemDTO request, Member member);
}
