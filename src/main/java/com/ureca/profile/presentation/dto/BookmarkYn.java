package com.ureca.profile.presentation.dto;

import lombok.Builder;
import lombok.Data;

// 북마크 여부
@Data
@Builder
public class BookmarkYn {
    private Boolean bookmarkYn;
}
