package com.jwt.JwtSecurity.config.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Builder
@ConfigurationProperties("storage")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StorageProperties {

    @Builder.Default
    private String baseLocation = "/base/uploads";

    @Builder.Default
    private String pdfLocation = "pdf";

    @Builder.Default
    private String excelLocation = "excel";

    @Builder.Default
    private String imageLocation = "image";

}
