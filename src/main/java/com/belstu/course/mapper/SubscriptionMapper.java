package com.belstu.course.mapper;

import com.belstu.course.dto.subscription.SubscriptionDto;
import com.belstu.course.model.Subscription;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    SubscriptionDto toDto(Subscription source);

    List<SubscriptionDto> toDto(List<Subscription> source);
}
