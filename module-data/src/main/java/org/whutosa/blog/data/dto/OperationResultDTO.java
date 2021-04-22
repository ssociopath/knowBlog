package org.whutosa.blog.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author bobo
 * @date 2021/4/21
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationResultDTO<ID> {
    private Boolean successful;
    private ID entityId;
    private String failedReason;

    public static <ID> OperationResultDTO<ID> success(ID id) {
        return OperationResultDTO.<ID>builder()
                .successful(true)
                .entityId(id)
                .failedReason("")
                .build();
    }

    public static <ID> OperationResultDTO<ID> fail(ID id, String failedReason) {
        return OperationResultDTO.<ID>builder()
                .successful(false)
                .entityId(id)
                .failedReason(failedReason)
                .build();
    }
}
