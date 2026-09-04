package com.caio.engine;

import java.util.List;

import com.caio.TemporaryDirectoryManager;
import com.caio.args.ApplicationArguments;
import com.caio.enums.BuildTool;
import com.caio.models.AnnotationMutationPoint;

public record EngineParams(List<AnnotationMutationPoint> amps, List<AnnotationMutationPoint> mainClasses,
                BuildTool buildTool, List<String> roles, List<String> authorities,
                ApplicationArguments applicationArguments, TemporaryDirectoryManager temporaryDirectoryManager) {

}
