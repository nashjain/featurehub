package io.featurehub.dacha.resource

import io.featurehub.dacha.InternalCache
import io.featurehub.dacha.api.DachaApiKeyService
import io.featurehub.mr.model.DachaFeatureValueItem
import io.featurehub.mr.model.DachaKeyDetailsResponse
import io.featurehub.mr.model.DachaPermissionResponse
import jakarta.inject.Inject
import jakarta.ws.rs.NotFoundException
import org.glassfish.hk2.api.Immediate
import java.util.*

@Immediate
class DachaApiKeyResource @Inject constructor(private val cache: InternalCache) : DachaApiKeyService {
  override fun getApiKeyDetails(eId: UUID, serviceAccountKey: String): DachaKeyDetailsResponse {
    val collection = cache.getFeaturesByEnvironmentAndServiceAccount(eId, serviceAccountKey) ?: throw NotFoundException()

    return DachaKeyDetailsResponse()
      .organizationId(collection.organizationId)
      .portfolioId(collection.portfolioId)
      .applicationId(collection.applicationId)
      .serviceKeyId(collection.serviceAccountId)
      .etag(collection.features.etag)
      .features(collection.features.features.toMutableList())
  }

  override fun getApiKeyPermissions(eId: UUID, serviceAccountKey: String, featureKey: String): DachaPermissionResponse {
    val collection = cache.getFeaturesByEnvironmentAndServiceAccount(eId, serviceAccountKey) ?: throw NotFoundException()

    val feature = collection.features.features.find { fv -> fv.feature?.key?.equals(featureKey) == true } ?: throw NotFoundException()

    return DachaPermissionResponse()
      .organizationId(collection.organizationId)
      .portfolioId(collection.portfolioId)
      .applicationId(collection.applicationId)
      .serviceKeyId(collection.serviceAccountId)
      .roles(collection.perms.permissions)
      .feature(DachaFeatureValueItem()
        .feature(feature.feature)
        .value(feature.value))
  }
}
