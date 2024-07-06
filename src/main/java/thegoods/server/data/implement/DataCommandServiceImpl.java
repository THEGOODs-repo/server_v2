package thegoods.server.data.implement;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import thegoods.server.common.exception.status.ErrorStatus;
import thegoods.server.common.utils.UtilService;
import thegoods.server.data.DataConverter;
import thegoods.server.data.dto.DataRequestDTO;
import thegoods.server.item.converter.ItemOptionConverter;
import thegoods.server.item.domain.*;
import thegoods.server.item.domain.repository.*;
import thegoods.server.item.implement.query.CategoryQueryService;
import thegoods.server.member.converter.MemberConverter;
import thegoods.server.member.domain.Member;
import thegoods.server.member.domain.MemberTerm;
import thegoods.server.member.domain.Term;
import thegoods.server.member.domain.repository.CategoryRepository;
import thegoods.server.member.domain.repository.MemberRepository;
import thegoods.server.member.domain.repository.ProfileImgRepository;
import thegoods.server.member.domain.repository.TermRepository;
import thegoods.server.member.exception.handler.MemberHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class DataCommandServiceImpl implements DataCommandService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder; // 비밀번호 암호화
    private final TermRepository termRepository;
    private final CategoryQueryService categoryQueryService;
    private final ItemRepository itemRepository;
    private final TagRepository tagRepository;
    private final ItemImgRepository itemImgRepository;
    private final ItemTagRepository itemTagRepository;
    private final ItemOptionRepository itemOptionRepository;
    private final CategoryTagRepository categoryTagRepository;
    private final UtilService utilService;


    @Override
    public Member addMember(DataRequestDTO.setMemberDTO request) {
        // userName 중복 체크
        memberRepository.findByNickname(request.getNickname())
                .ifPresent(user -> {
                    throw new MemberHandler(ErrorStatus.MEMBER_NICKNAME_DUPLICATED);
                });


        //저장
        Member member = DataConverter.toTestMember(request, encoder);

        // 약관동의 저장 로직
        HashMap<Term, Boolean> termMap = new HashMap<>();
        for (int i = 0; i < request.getMemberTerm().size(); i++) {
            termMap.put(termRepository.findById(i + 1L).orElseThrow(() -> new MemberHandler(ErrorStatus.TERM_NOT_FOUND)), request.getMemberTerm().get(i));

        }

        List<MemberTerm> memberTermList = MemberConverter.toMemberTermList(termMap);
        memberTermList.forEach(memberTerm -> {
            memberTerm.setMember(member);
        });

        memberRepository.save(member);
        return member;
    }

    @Override
    public Item addItem(Member member, DataRequestDTO.setItemDTO request, MultipartFile itemThumbnail, List<MultipartFile> multipartFileList) {
        Item newItem = DataConverter.toTestItem(request);

        // 판매자 설정
        newItem.setMember(member);

        // 카테고리 설정
        Category category = categoryQueryService.findCategoryById(request.getCategory());
        newItem.setCategory(category);

        Item savedItem = itemRepository.save(newItem);

        // 태그 생성 및 연관관계 매핑
        if (request.getItemTag() != null) {
            request.getItemTag().forEach(tagName -> {
                if (!tagRepository.existsByName(tagName)) { // 기존에 존재하지 않는 태그인 경우
                    Tag newTag = DataConverter.toTag(tagName);
                    Tag savedTag = tagRepository.save(newTag);

                    ItemTag itemTag = DataConverter.toItemTag();
                    itemTag.setItem(savedItem);
                    itemTag.setTag(savedTag);

                    itemTagRepository.save(itemTag);

                    CategoryTag categoryTag = DataConverter.toCategoryTag();
                    categoryTag.setTag(savedTag);
                    categoryTag.setCategory(category);

                    categoryTagRepository.save(categoryTag);

                } else { // 기존에 존재하는 태그인 경우
                    // itemTag 설정
                    Tag tag = tagRepository.findFirstByNameOrderByCreatedAtAsc(tagName).get();
                    ItemTag itemTag = DataConverter.toItemTag();
                    itemTag.setItem(savedItem);
                    itemTag.setTag(tag);

                    itemTagRepository.save(itemTag);

                    // categoryTag 설정
                    Optional<CategoryTag> categoryTag = categoryTagRepository.findByCategoryAndTag(category, tag);
                    if (categoryTag.isEmpty()) {// 카테고리-태그 매핑 존재하지 않는 경우
                        // 새로운 카테고리-태그 매핑 생성
                        CategoryTag newCategoryTag = DataConverter.toCategoryTag();
                        newCategoryTag.setTag(tag);
                        newCategoryTag.setCategory(category);

                        categoryTagRepository.save(newCategoryTag);
                    }
                }
            });

            savedItem.updateTagCounts(request.getItemTag().size());
        }

        // 이미지 등록 및 연관관계 매핑
        // 썸네일 이미지
        String thumbnailUrl = utilService.uploadS3Img("item", itemThumbnail);
        ItemImg thumbnailItemImg = DataConverter.toItemImg(thumbnailUrl, true);
        thumbnailItemImg.setItem(savedItem);
        itemImgRepository.save(thumbnailItemImg);

        // 일반 상품 이미지
        if (multipartFileList != null) {
            List<ItemImg> itemImgList = multipartFileList.stream().map(multipartFile -> {
                String itemImgUrl = utilService.uploadS3Img("item", multipartFile);

                ItemImg itemImg = DataConverter.toItemImg(itemImgUrl, false);
                itemImg.setItem(savedItem);
                return itemImg;
            }).collect(Collectors.toList());

            itemImgRepository.saveAll(itemImgList);
        }


        // 상품 옵션 등록
        List<ItemOption> itemOptionList = request.getItemOptionList().stream().map(itemOptionDTO -> {
            ItemOption itemOption = ItemOptionConverter.toItemOption(itemOptionDTO);
            itemOption.setItem(savedItem);
            return itemOption;
        }).collect(Collectors.toList());

        itemOptionRepository.saveAll(itemOptionList);

        return savedItem;
    }
}
