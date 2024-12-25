package com.jwt.JwtSecurity.service.iservice;

import java.nio.file.Path;

@FunctionalInterface
public interface IFilePathService {

    Path getDestinationPath();

}
