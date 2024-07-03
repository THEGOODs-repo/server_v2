package thegoods.server.item.converter;

import thegoods.server.item.domain.Tag;
import thegoods.server.item.domain.TagSearch;
import thegoods.server.member.domain.Member;

import java.util.List;
import java.util.stream.Collectors;

public class TagSearchConverter {

    public static List<TagSearch> toTagSearchList(List<Tag> tagList, Member member) {

        return tagList.stream()
                .map(tag ->
                        TagSearch.builder()
                                .tag(tag)
                                .member(member)
                                .build()
                ).collect(Collectors.toList());
    }
}
