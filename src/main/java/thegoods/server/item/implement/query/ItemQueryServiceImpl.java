package thegoods.server.item.implement.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thegoods.server.common.exception.status.ErrorStatus;
import thegoods.server.item.converter.TagSearchConverter;
import thegoods.server.item.domain.*;
import thegoods.server.item.domain.repository.*;
import thegoods.server.member.domain.Member;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemQueryServiceImpl implements ItemQueryService {

    private final ItemRepository itemRepository;
    private final ItemOptionRepository itemOptionRepository;
    private final ItemViewRepository itemViewRepository;
    private final ItemTagRepository itemTagRepository;
    private final TagSearchRepository tagSearchRepository;
    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;

    Integer pageSize = 10;

    @Override
    public Optional<Item> findItemById(Long id) {
        return itemRepository.findById(id);
    }

    @Override
    public Optional<ItemOption> findItemOptionById(Long id) {
        return itemOptionRepository.findById(id);
    }

    @Override
    public boolean isExistItem(Long id) {
        return itemRepository.existsById(id);
    }

    @Override
    public boolean isExistItemOption(Long id) {
        return itemOptionRepository.existsById(id);
    }

    @Override
    @Transactional
    public Page<Item> getMyItemList(Member member, Integer page) {
        Page<Item> itemPage = itemRepository.findAllByMember(member, PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt")));
        return itemPage;
    }

    @Override
    public Page<Item> getTodayItemList(Integer page) {

        Long[] idArray = {4L, 8L, 10L, 11L, 14L, 15L, 18L, 19L, 21L, 24L, 25L, 26L, 29L, 30L, 33L, 38L, 39L, 41L, 42L, 53L,
                55L, 71L, 76L, 79L, 80L, 85L, 91L, 92L, 94L, 95L, 96L, 97L, 98L, 99L, 105L, 107L, 120L, 132L, 139L, 143L,
                158L, 161L, 165L, 170L, 171L, 174L, 177L, 178L, 198L, 201L, 204L, 205L, 236L, 238L, 249L, 266L, 269L, 271L,
                272L, 275L, 293L, 299L, 301L, 303L, 310L, 313L, 316L, 317L, 322L, 328L, 329L, 331L, 333L, 334L, 337L, 341L,
                343L, 346L, 347L, 348L, 349L, 352L, 360L, 366L, 367L, 379L, 381L, 387L, 392L, 393L, 397L, 399L, 401L, 405L,
                406L, 408L};

        ArrayList<Long> itemIdList = new ArrayList<>(Arrays.asList(idArray));
        return itemRepository.findAllByIdIn(itemIdList, PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "viewCount")));
    }

    @Override
    @Transactional
    public Page<Item> getSimilarItemList(Long itemId, Member member, Integer page) {
        Page<Item> similarItemList = null;

        if (!member.getNickname().equals("no_login_user")) {
            List<ItemView> lastView = itemViewRepository.findAllByMemberIdOrderByCreatedAtDesc(member.getId());
            if (!lastView.isEmpty()) {
                Item viewedItem = itemRepository.findById(lastView.get(0).getItem().getId()).orElseThrow(() -> new ItemHandler(ErrorStatus.ITEM_NOT_FOUND));
                Category category = viewedItem.getCategory();
                similarItemList = itemRepository.findAllByCategoryName(category.getName(), PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "viewCount")));
            } else {
                //추후 수정
                similarItemList = itemRepository.findAllByCategoryName("창작", PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "viewCount")));
            }
        } else {
            Item viewedItem = itemRepository.findById(itemId).orElseThrow(() -> new ItemHandler(ErrorStatus.ITEM_NOT_FOUND));
            Category category = viewedItem.getCategory();
            similarItemList = itemRepository.findAllByCategoryName(category.getName(), PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "viewCount")));
        }

        return similarItemList;
    }

    @Override
    public Page<Item> getTopSaleItemList(Integer page) {

        return itemRepository.findAll(PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "salesCount")));

    }

    @Override
    public Page<Item> getSteadySaleItemList(Integer pageIdx) {
        LocalDate date = LocalDate.now().minusMonths(6);
        return itemRepository.findAllByStartDateLessThanEqual(date, PageRequest.of(pageIdx, pageSize, Sort.by(Sort.Direction.DESC, "viewCount")));
    }

    @Override
    @Transactional
    public Page<Item> getItemByTagCount(Integer page) {
//        List<ConsultResultDTO> consultResults = itemTagRepository.countTagsByTagId();
//
//        List<Item> itemList = consultResults.stream()
//                .map(consultResult -> itemRepository.findById(consultResult.getItemId()).orElseThrow(() -> new ItemHandler(ErrorStatus.ITEM_NOT_FOUND)))
//                .collect(Collectors.toList());
//
//        PageRequest pageRequest = PageRequest.of(page, 10);
//        int start = (int) pageRequest.getOffset();
//        int end = Math.min((start + pageRequest.getPageSize()), itemList.size());
//        Page<Item> itemPage = new PageImpl<>(itemList.subList(start, end), pageRequest, itemList.size());

        Page<Item> itemPage = null;
        itemPage = itemRepository.findAll(PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "tagsCount")));

        return itemPage;
    }

    @Override
    @Transactional
    public Page<Item> getItemByDeliveryDate(Integer page) {
        Page<Item> itemPage = itemRepository.findAll(PageRequest.of(page, pageSize, Sort.by(Sort.Direction.ASC, "deliveryDate")));
        return itemPage;
    }

    @Override
    @Transactional
    public Page<Item> searchItem(Member member, String itemName, String categoryName, String sellerName, List<String> tagName, String type, Integer page) {
        Page<Item> itemPage = null;
        Integer searchCondition = 0;
        String searchType = "temp";
        if (type.equals("new")) {
            searchType = "createdAt";
        } else if (type.equals("popular")) {
            searchType = "viewCount";
        } else if (type.equals("dibsCount")) {
            searchType = "dibsCount";
        } else if (type.equals("salesCount")) {
            searchType = "salesCount";
        } else if (type.equals("highPrice")) {
            searchType = "price";
        } else if (type.equals("lowPrice")) {
            searchType = "lowPrice";
        } else if (type.equals("reviewCount")) {
            searchType = "reviewCount";
        } else {
            throw new ItemHandler(ErrorStatus.MAIN_ITEM_SEARCH_TYPE_ERROR);
        }

        if (itemName != null) {
            if (searchType.equals("lowPrice")) {
                itemPage = itemRepository.findAllByNameContaining(itemName, PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "price")));
            } else if (searchType.equals("reviewCount")) {
                itemPage = itemRepository.findAllByItemNameContainingOrdOrderByReviewListSizeDesc(itemName, PageRequest.of(page, 10));
            } else {
                itemPage = itemRepository.findAllByNameContaining(itemName, PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, searchType)));
            }
            searchCondition++;
        }
        if (categoryName != null) {
            Category category = categoryRepository.findByName(categoryName).orElseThrow(() -> new CategoryHandler(ErrorStatus.CATEGORY_NOT_FOUND));
            if (searchType.equals("lowPrice")) {
                itemPage = itemRepository.findAllByCategoryName(category.getName(), PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "price")));
            } else if (searchType.equals("reviewCount")) {
                itemPage = itemRepository.findAllByCategoryNameOrderByReviewListSizeDesc(category.getName(), PageRequest.of(page, 10));
            } else {
                itemPage = itemRepository.findAllByCategoryName(category.getName(), PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, searchType)));
            }
            searchCondition++;
        }
        if (sellerName != null) {
            Member seller = memberRepository.findByNickname(sellerName).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

            if (searchType.equals("lowPrice")) {
                itemPage = itemRepository.findAllByMember(seller, PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "price")));
            } else if (searchType.equals("reviewCount")) {
                itemPage = itemRepository.findAllBySellerNameOrderByReviewListSizeDesc(seller.getNickname(), PageRequest.of(page, 10));
            } else {
                itemPage = itemRepository.findAllByMember(seller, PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, searchType)));
            }
            searchCondition++;
        }
        if (tagName != null) {
            //이 방식대로 tag 탐색시 잘못 탐색됨. tag 전체를 가지고 있는 경우만 찾도록 해야함.
            List<String> tagNameWithoutHashtag = tagName.stream()
                    .map(tag -> tag.replace("#", ""))
                    .collect(Collectors.toList());

            List<Tag> tags = tagNameWithoutHashtag.stream()
                    .map(tag -> tagRepository.findByName(tag).orElseThrow(() -> new TagHandler(ErrorStatus.TAG_NOT_FOUND)))
                    .collect(Collectors.toList());

            if (!tags.isEmpty()) {
                // 아이템을 검색하는 메소드 호출
                List<Item> itemList = itemRepository.findAllByItemTagListTagIn(tags);
                searchCondition++;

                // 중복된 아이템을 아이디를 기준으로 제거
                List<Item> distinctItems = itemList.stream()
                        .collect(Collectors.toMap(Item::getId, item -> item, (existing, replacement) -> existing))
                        .values()
                        .stream()
                        .collect(Collectors.toList());

                PageRequest pageRequest = PageRequest.of(page, 10);

                // 중복이 제거된 아이템으로 새로운 Page 생성
                if (type.equals("new")) {
                    distinctItems.sort(Comparator.comparing(Item::getCreatedAt).reversed());

                    //pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
                } else if (type.equals("popular")) {
                    distinctItems.sort(Comparator.comparing(Item::getViewCount).reversed());

                    //pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "viewCount"));
                } else if (type.equals("dibsCount")) {
                    distinctItems.sort(Comparator.comparing(Item::getDibsCount).reversed());

                    //pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "dibsCount"));
                } else if (type.equals("salesCount")) {
                    distinctItems.sort(Comparator.comparing(Item::getSalesCount).reversed());

                    //pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "salesCount"));
                } else if (type.equals("highPrice")) {
                    distinctItems.sort(Comparator.comparing(Item::getPrice).reversed());

                    //pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "price"));
                }
                pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, searchType));
                if (type.equals("lowPrice")) {
                    distinctItems.sort(Comparator.comparing(Item::getPrice));

                    pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "price"));
                } else if (type.equals("reviewCount")) {
                    List<Item> itemListReviewCount = itemRepository.findAllByItemTagListTagInOrderByReviewListSizeDesc(tags);

                    // 중복된 아이템을 아이디를 기준으로 제거
                    distinctItems = itemListReviewCount.stream()
                            .collect(Collectors.toMap(Item::getId, item -> item, (existing, replacement) -> existing))
                            .values()
                            .stream()
                            .collect(Collectors.toList());

                    pageRequest = PageRequest.of(page, 10);
                }

                int start = (int) pageRequest.getOffset();
                int end = Math.min((start + pageRequest.getPageSize()), distinctItems.size());
                itemPage = new PageImpl<>(distinctItems.subList(start, end), pageRequest, distinctItems.size());
                //itemPage = new PageImpl<>(distinctItems, itemPage.getPageable(), distinctItems.size());
            }
            List<TagSearch> tagSearchList = TagSearchConverter.toTagSearchList(tags, member);

            tagSearchList.forEach(tagSearch -> {
                tagSearch.setMember(member);
            });

            tagSearchRepository.saveAll(tagSearchList);
        }
        if (searchCondition > 1) {
            throw new ItemHandler(ErrorStatus.ITEM_SEARCH_ERROR);
        }

        return itemPage;
    }

    @Override
    public Page<Item> getMainItem(String type, Integer pageIdx) {
        Page<Item> itemPage = null;
        if (type.equals("new")) {
            itemPage = itemRepository.findAll(PageRequest.of(pageIdx, pageSize, Sort.by(Sort.Direction.DESC, "createdAt")));
        } else if (type.equals("popular")) {
            itemPage = itemRepository.findAll(PageRequest.of(pageIdx, pageSize, Sort.by(Sort.Direction.DESC, "viewCount")));
        } else if (type.equals("last")) {
            itemPage = itemRepository.findAllByEndDateGreaterThanEqual(LocalDate.now(), PageRequest.of(pageIdx, pageSize, Sort.by(Sort.Direction.ASC, "endDate")));
        } else {
            throw new ItemHandler(ErrorStatus.MAIN_ITEM_SEARCH_TYPE_ERROR);
        }

        return itemPage;
    }

    @Override
    public Page<Item> getRelatedItem(Long itemId, Integer pageIdx) {

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemHandler(ErrorStatus.ITEM_NOT_FOUND));

        Category category = item.getCategory();

        Page<Item> itemPage = itemRepository.findAllByCategory(category, PageRequest.of(pageIdx, pageSize, Sort.by(Sort.Direction.DESC, "viewCount")));

        return itemPage;
    }
}
