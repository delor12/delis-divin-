package com.delisdivin.tenant;

import lombok.extern.slf4j.Slf4j;

/**
 * Thread-safe context for holding the current tenant's (restaurant's) identifier.
 */
@Slf4j
public class TenantContext {

    private static final ThreadLocal<Long> CURRENT_TENANT = new ThreadLocal<>();

    public static void setCurrentTenant(Long tenantId) {
        log.debug("Setting tenant context to: {}", tenantId);
        CURRENT_TENANT.set(tenantId);
    }

    public static Long getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    public static void clear() {
        log.debug("Clearing tenant context");
        CURRENT_TENANT.remove();
    }
}
