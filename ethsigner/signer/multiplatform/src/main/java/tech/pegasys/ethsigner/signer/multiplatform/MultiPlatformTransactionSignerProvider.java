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

import com.google.common.base.Charsets;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import tech.pegasys.ethsigner.TransactionSignerInitializationException;
import tech.pegasys.ethsigner.core.signing.TransactionSigner;
import tech.pegasys.ethsigner.core.signing.TransactionSignerProvider;
import tech.pegasys.ethsigner.signer.azure.AzureKeyVaultAuthenticator;
import tech.pegasys.ethsigner.signer.filebased.FileBasedSignerFactory;
import tech.pegasys.ethsigner.signer.azure.AzureKeyVaultTransactionSignerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MultiPlatformTransactionSignerProvider implements TransactionSignerProvider {

  private static final Logger LOG = LogManager.getLogger();

  private final SigningMetadataTomlConfigLoader signingMetadataTomlConfigLoader;

  MultiPlatformTransactionSignerProvider(
      final SigningMetadataTomlConfigLoader signingMetadataTomlConfigLoader) {
    this.signingMetadataTomlConfigLoader = signingMetadataTomlConfigLoader;
  }

  @Override
  public Optional<TransactionSigner> getSigner(final String address) {
    return signingMetadataTomlConfigLoader.loadMetadataForAddress(address).map(this::createSigner);
  }

  @Override
  public Set<String> availableAddresses() {
    return signingMetadataTomlConfigLoader.loadAvailableSigningMetadataTomlConfigs().stream()
        .map(this::createSigner)
        .filter(Objects::nonNull)
        .map(TransactionSigner::getAddress)
        .collect(Collectors.toSet());
  }


  private static String readSecretFromFile(final Path path) throws IOException {
    final byte[] fileContent = Files.readAllBytes(path);
    return new String(fileContent, Charsets.UTF_8);
  }

  private TransactionSigner createSigner(final AzureSigningMetadataFile signingMetadataFile) {
    try {

      try {
        final String clientSecret = readSecretFromFile(signingMetadataFile.getClientSecretPath());
      } catch (final IOException e) {
        throw new TransactionSignerInitializationException(READ_SECRET_FILE_ERROR, e);
      }

      final AzureKeyVaultAuthenticator authenticator = new AzureKeyVaultAuthenticator();
      final KeyVaultClientCustom client =
          authenticator.getAuthenticatedClient(clientId, clientSecret);
      final AzureKeyVaultTransactionSignerFactory factory =
          new AzureKeyVaultTransactionSignerFactory(keyvaultName, client);
      return factory.createSigner(keyName, keyVersion);


      final TransactionSigner signer =
          AzureKeyVaultTransactionSignerFactory.createSigner(
              signingMetadataFile.getKeyPath(), signingMetadataFile.getPasswordPath());
      LOG.debug("Loaded azure signer with key '{}'", signingMetadataFile.getKeyName());
      return signer;
    } catch (final TransactionSignerInitializationException e) {
      LOG.warn(
          "Unable to load signer with key '{}'", signingMetadataFile.getKeyName(), e);
      return null;
    }
  }
  private TransactionSigner createSigner(final FileBasedSigningMetadataFile signingMetadataFile) {
    try {
      final TransactionSigner signer =
          FileBasedSignerFactory.createSigner(
              signingMetadataFile.getKeyPath(), signingMetadataFile.getPasswordPath());
      LOG.debug("Loaded signer with key '{}'", signingMetadataFile.getKeyPath().getFileName());
      return signer;
    } catch (final TransactionSignerInitializationException e) {
      LOG.warn(
          "Unable to load signer with key '{}'", signingMetadataFile.getKeyPath().getFileName(), e);
      return null;
    }
  }
}
