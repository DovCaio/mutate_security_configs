package com.caio.engine.workers;

import java.nio.file.Path;

import com.caio.TemporaryDirectoryManager;
import com.caio.args.ApplicationArguments;
import com.caio.enums.BuildTool;

public record DirectoryParallelExecutorParams(int workerCount, Path originalDirectory, BuildTool buildTool,
                ApplicationArguments applicationArguments, TemporaryDirectoryManager temporaryDirectoryManager) {

}
