/*
 * Copyright 2019 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package tech.pegasys.ethsigner.signer.multiplatform;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.pegasys.ethsigner.signer.multiplatform.MetadataFileFixture.CONFIG_FILE_EXTENSION;
import static tech.pegasys.ethsigner.signer.multiplatform.MetadataFileFixture.LOWERCASE_ADDRESS;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AzureSigningMetadataFileTest {

  @TempDir Path configsDirectory;
  private final String keyVersion = "v1";
  private final String keyName = "keyName";
  private final String keyVaultName = "keyVaultName";
  private final String secret = "client-secret";
  private Path secretPath;

  @BeforeEach
  void beforeEach() {
    secretPath = configsDirectory.resolve(secret);
  }

  @Test
  void matchingMetadataFileWithoutPrefixShouldHaveExpectedName() {
    final AzureSigningMetadataFile fileBasedSigningMetadataFile =
        new AzureSigningMetadataFile(
            "azure-" + LOWERCASE_ADDRESS + CONFIG_FILE_EXTENSION,
            secretPath,
            keyName,
            keyVersion,
            keyVaultName);

    assertThat(fileBasedSigningMetadataFile.getFilename()).matches("azure-" + LOWERCASE_ADDRESS);
    assertThat(fileBasedSigningMetadataFile.getClientSecretPath().toFile().toString())
        .matches(secretPath.toAbsolutePath().toString());
    assertThat(fileBasedSigningMetadataFile.getKeyName()).matches(keyName);
    assertThat(fileBasedSigningMetadataFile.getKeyVersion()).matches(keyVersion);
    assertThat(fileBasedSigningMetadataFile.getKeyVaultName()).matches(keyVaultName);
  }
}
