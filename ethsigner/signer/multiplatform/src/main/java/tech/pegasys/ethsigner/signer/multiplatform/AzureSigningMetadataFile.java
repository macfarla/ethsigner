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

import java.nio.file.Path;

import com.google.common.base.Objects;

class AzureSigningMetadataFile {

  private final Path clientSecretPath;
  private final String keyName;
  private final String keyVersion;
  private final String keyVaultName;
  private final String filename;

  public AzureSigningMetadataFile(
      final String filename,
      final Path clientSecretPath,
      final String keyName,
      final String keyVersion,
      final String keyVaultName) {
    this.filename = getFilenameWithoutExtension(filename);
    this.clientSecretPath = clientSecretPath;
    this.keyName = keyName;
    this.keyVersion = keyVersion;
    this.keyVaultName = keyVaultName;
  }

  String getFilename() {
    return filename;
  }

  Path getClientSecretPath() {
    return clientSecretPath;
  }

  String getKeyName() {
    return keyName;
  }

  String getKeyVersion() {
    return keyVersion;
  }

  String getKeyVaultName() {
    return keyVaultName;
  }

  private String getFilenameWithoutExtension(final String filename) {
    if (filename.endsWith(".toml")) {
      return filename.replaceAll("\\.toml", "");
    } else {
      throw new IllegalArgumentException("Invalid TOML config filename extension: " + filename);
    }
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final AzureSigningMetadataFile that = (AzureSigningMetadataFile) o;
    return Objects.equal(filename, that.filename)
        && Objects.equal(clientSecretPath, that.clientSecretPath)
        && Objects.equal(keyName, that.keyName)
        && Objects.equal(keyVersion, that.keyVersion)
        && Objects.equal(keyVaultName, that.keyVaultName);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(filename, clientSecretPath, keyName);
  }
}
