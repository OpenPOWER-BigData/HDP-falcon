/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.falcon.hive;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.falcon.hive.exception.HiveReplicationException;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tool Options.
 */
public class HiveDROptions {
    private final Map<HiveDRArgs, String> context;

    protected HiveDROptions(Map<HiveDRArgs, String> context) {
        this.context = context;
    }

    public String getValue(HiveDRArgs arg) {
        return context.get(arg);
    }

    public Map<HiveDRArgs, String> getContext() {
        return context;
    }

    public String getSourceMetastoreUri() {
        return context.get(HiveDRArgs.SOURCE_METASTORE_URI);
    }

    public String getSourceMetastoreKerberosPrincipal() {
        return context.get(HiveDRArgs.SOURCE_HIVE_METASTORE_KERBEROS_PRINCIPAL);
    }

    public String getSourceHive2KerberosPrincipal() {
        return context.get(HiveDRArgs.SOURCE_HIVE2_KERBEROS_PRINCIPAL);
    }

    public List<String> getSourceDatabases() {
        return Arrays.asList(context.get(HiveDRArgs.SOURCE_DATABASE).trim().split(","));
    }

    public List<String> getSourceTables() {
        return Arrays.asList(context.get(HiveDRArgs.SOURCE_TABLE).trim().split(","));
    }

    public String getSourceStagingPath() throws HiveReplicationException {
        if (StringUtils.isNotEmpty(context.get(HiveDRArgs.SOURCE_STAGING_PATH))) {
            return context.get(HiveDRArgs.SOURCE_STAGING_PATH) + File.separator + getJobName();
        }
        throw new HiveReplicationException("Source StagingPath cannot be empty");
    }

    public String getSourceWriteEP() {
        return context.get(HiveDRArgs.SOURCE_NN);
    }

    public String getSourceNNKerberosPrincipal() {
        return context.get(HiveDRArgs.SOURCE_NN_KERBEROS_PRINCIPAL);
    }

    public String getTargetWriteEP() {
        return context.get(HiveDRArgs.TARGET_NN);
    }

    public String getTargetMetastoreUri() {
        return context.get(HiveDRArgs.TARGET_METASTORE_URI);
    }

    public String getTargetNNKerberosPrincipal() {
        return context.get(HiveDRArgs.TARGET_NN_KERBEROS_PRINCIPAL);
    }

    public String getTargetMetastoreKerberosPrincipal() {
        return context.get(HiveDRArgs.TARGET_HIVE_METASTORE_KERBEROS_PRINCIPAL);
    }
    public String getTargetHive2KerberosPrincipal() {
        return context.get(HiveDRArgs.TARGET_HIVE2_KERBEROS_PRINCIPAL);
    }

    public String getTargetStagingPath() throws HiveReplicationException {
        if (StringUtils.isNotEmpty(context.get(HiveDRArgs.TARGET_STAGING_PATH))) {
            return context.get(HiveDRArgs.TARGET_STAGING_PATH) + File.separator + getJobName();
        }
        throw new HiveReplicationException("Target StagingPath cannot be empty");
    }

    public String getReplicationMaxMaps() {
        return context.get(HiveDRArgs.REPLICATION_MAX_MAPS);
    }

    public String getJobName() {
        return context.get(HiveDRArgs.JOB_NAME);
    }

    public int getMaxEvents() {
        return Integer.valueOf(context.get(HiveDRArgs.MAX_EVENTS));
    }

    public boolean shouldKeepHistory() {
        return Boolean.valueOf(context.get(HiveDRArgs.KEEP_HISTORY));
    }

    public String getJobClusterWriteEP() {
        return context.get(HiveDRArgs.JOB_CLUSTER_NN);
    }

    public String getJobClusterNNPrincipal() {
        return context.get(HiveDRArgs.JOB_CLUSTER_NN_KERBEROS_PRINCIPAL);
    }

    public void setSourceStagingDir(String path) {
        context.put(HiveDRArgs.SOURCE_STAGING_PATH, path);
    }

    public void setTargetStagingDir(String path) {
        context.put(HiveDRArgs.TARGET_STAGING_PATH, path);
    }

    public String getExecutionStage() {
        return context.get(HiveDRArgs.EXECUTION_STAGE);
    }

    public boolean isTDEEncryptionEnabled() {
        return StringUtils.isEmpty(context.get(HiveDRArgs.TDE_ENCRYPTION_ENABLED))
                ? false : Boolean.valueOf(context.get(HiveDRArgs.TDE_ENCRYPTION_ENABLED));
    }

    public boolean shouldBlock() {
        return true;
    }

    public static HiveDROptions create(String[] args) throws ParseException {
        Map<HiveDRArgs, String> options = new HashMap<HiveDRArgs, String>();

        CommandLine cmd = getCommand(args);
        for (HiveDRArgs arg : HiveDRArgs.values()) {
            String optionValue = arg.getOptionValue(cmd);
            if (StringUtils.isNotEmpty(optionValue)) {
                options.put(arg, optionValue);
            }
        }

        return new HiveDROptions(options);
    }

    private static CommandLine getCommand(String[] arguments) throws ParseException {
        Options options = new Options();

        for (HiveDRArgs arg : HiveDRArgs.values()) {
            addOption(options, arg, arg.isRequired());
        }

        return new GnuParser().parse(options, arguments, false);
    }

    private static void addOption(Options options, HiveDRArgs arg, boolean isRequired) {
        Option option = arg.getOption();
        option.setRequired(isRequired);
        options.addOption(option);
    }
}
