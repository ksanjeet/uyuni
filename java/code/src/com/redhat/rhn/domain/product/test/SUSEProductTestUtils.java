/**
 * Copyright (c) 2014 SUSE LLC
 *
 * This software is licensed to you under the GNU General Public License,
 * version 2 (GPLv2). There is NO WARRANTY for this software, express or
 * implied, including the implied warranties of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
 * along with this software; if not, see
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
 *
 * Red Hat trademarks are not licensed under GPLv2. No permission is
 * granted to use or replicate Red Hat trademarks that are incorporated
 * in this software or its documentation.
 */
package com.redhat.rhn.domain.product.test;

import com.redhat.rhn.common.db.datasource.ModeFactory;
import com.redhat.rhn.common.db.datasource.WriteMode;
import com.redhat.rhn.common.hibernate.HibernateFactory;
import com.redhat.rhn.domain.channel.Channel;
import com.redhat.rhn.domain.channel.ChannelFactory;
import com.redhat.rhn.domain.channel.ChannelFamily;
import com.redhat.rhn.domain.channel.test.ChannelFactoryTest;
import com.redhat.rhn.domain.channel.test.ChannelFamilyFactoryTest;
import com.redhat.rhn.domain.product.SUSEProduct;
import com.redhat.rhn.domain.rhnpackage.PackageFactory;
import com.redhat.rhn.domain.server.InstalledProduct;
import com.redhat.rhn.domain.server.Server;
import com.redhat.rhn.testing.TestUtils;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Utility methods for creating SUSE related test data.
 */
public class SUSEProductTestUtils {

    /**
     * Not to be instantiated.
     */
    private SUSEProductTestUtils() {
    }

    /**
     * Create a SUSE product (which is different from a {@link com.redhat.rhn.domain.channel.ChannelProduct}).
     * @param family the channel family
     * @return the newly created SUSE product
     * @throws Exception if anything goes wrong
     */
    public static SUSEProduct createTestSUSEProduct(ChannelFamily family) throws Exception {
        SUSEProduct product = new SUSEProduct();
        String name = TestUtils.randomString().toLowerCase();
        product.setName(name);
        product.setVersion("1");
        product.setFriendlyName("SUSE Test product " + name);
        product.setArch(PackageFactory.lookupPackageArchByLabel("x86_64"));
        product.setRelease("test");
        product.setProductId(new Random().nextInt(999999));

        TestUtils.saveAndReload(product);

        return product;
    }

    /**
     * Create a vendor channel (org is null) for testing.
     * @return vendor channel for testing
     * @throws Exception
     */
    public static Channel createTestVendorChannel() throws Exception {
        Channel c = ChannelFactoryTest.createTestChannel(null,
                ChannelFamilyFactoryTest.createTestChannelFamily());
        ChannelFactory.save(c);
        return c;
    }

    /**
     * For a given file, delete it in case it is a temp file.
     * @param file test file to delete
     */
    public static void deleteIfTempFile(File file) {
        if (file.exists() && file.getAbsolutePath().startsWith(
                System.getProperty("java.io.tmpdir") + File.separatorChar)) {
            file.delete();
        }
    }

    /**
     * Create a SUSE product channel (that is, links a channel to a SUSE
     * product, eg. a row in suseproductchannel).
     * @param channel the channel
     * @param product the SUSE product
     */
    public static void createTestSUSEProductChannel(Channel channel, SUSEProduct product) {
        WriteMode m = ModeFactory.getWriteMode("test_queries",
                "insert_into_suseproductchannel");

        Channel parentChannel = channel.getParentChannel();

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("product_id", product.getId());
        parameters.put("channel_id", channel.getId());
        parameters.put("channel_label", channel.getLabel());
        parameters.put("parent_channel_label",
                parentChannel != null ? parentChannel.getLabel() : null);

        m.executeUpdate(parameters);
        HibernateFactory.getSession().flush();
    }

    /**
     * Marks one SUSE product as a possible upgrade of another.
     * @param from the first SUSE product
     * @param to the second SUSE product
     */
    public static void createTestSUSEUpgradePath(SUSEProduct from, SUSEProduct to) {
        WriteMode m = ModeFactory.getWriteMode("test_queries",
                "insert_into_suseupgradepath");

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("from_pdid", from.getId());
        parameters.put("to_pdid", to.getId());

        m.executeUpdate(parameters);
        HibernateFactory.getSession().flush();
    }

    /**
     * Link a {@link com.redhat.rhn.domain.product.SUSEProduct}
     * with a {@link com.redhat.rhn.domain.server.Server}.
     * @param product the product
     * @param server the server
     */
    public static void installSUSEProductOnServer(SUSEProduct product, Server server) {
        // Insert into suseInstalledProduct
        InstalledProduct prd = new InstalledProduct();
        prd.setName(product.getName());
        prd.setVersion(product.getVersion());
        prd.setRelease(product.getRelease());
        prd.setArch(product.getArch());
        prd.setBaseproduct(true);

        Set<InstalledProduct> products = new HashSet<>();
        products.add(prd);

        // Insert into suseServerInstalledProduct
        server.setInstalledProducts(products);
        HibernateFactory.getSession().flush();
    }

    /**
     * Create two standard SUSE Vendor products.
     *
     * SLES12 SP1 x86_64
     * SLE-HA12 SP1 x86_64
     */
    public static void createVendorSUSEProducts() {
        SUSEProduct product = new SUSEProduct();
        product.setName("sles");
        product.setVersion("12.1");
        product.setFriendlyName("SUSE Linux Enterprise Server 12 SP1");
        product.setArch(PackageFactory.lookupPackageArchByLabel("x86_64"));
        product.setProductId(1322);
        TestUtils.saveAndFlush(product);

        product = new SUSEProduct();
        product.setName("sle-ha");
        product.setVersion("12.1");
        product.setFriendlyName("SUSE Linux Enterprise High Availability Extension 12 SP1");
        product.setArch(PackageFactory.lookupPackageArchByLabel("x86_64"));
        product.setProductId(1324);
        TestUtils.saveAndFlush(product);
    }

    /**
     * Create standard SUSE Vendor Entitlement products.
     */
    public static void createVendorEntitlementProducts() {
        SUSEProduct product = new SUSEProduct();
        product.setName("suse-manager-mgmt-unlimited-virtual-z");
        product.setVersion("1.2");
        product.setFriendlyName("SUSE Manager Mgmt Unlimited Virtual Z 1.2");
        product.setProductId(1200);
        TestUtils.saveAndFlush(product);

        product = new SUSEProduct();
        product.setName("suse-manager-prov-unlimited-virtual-z");
        product.setVersion("1.2");
        product.setFriendlyName("SUSE Manager Prov Unlimited Virtual Z 1.2");
        product.setProductId(1205);
        TestUtils.saveAndFlush(product);

        product = new SUSEProduct();
        product.setName("suse-manager-mgmt-unlimited-virtual");
        product.setVersion("1.2");
        product.setFriendlyName("SUSE Manager Mgmt Unlimited Virtual 1.2");
        product.setProductId(1078);
        TestUtils.saveAndFlush(product);

        product = new SUSEProduct();
        product.setName("suse-manager-prov-unlimited-virtual");
        product.setVersion("1.2");
        product.setFriendlyName("SUSE Manager Prov Unlimited Virtual 1.2");
        product.setProductId(1204);
        TestUtils.saveAndFlush(product);

        product = new SUSEProduct();
        product.setName("suse-manager-mgmt-single");
        product.setVersion("1.2");
        product.setFriendlyName("SUSE Manager Mgmt Single 1.2");
        product.setProductId(1076);
        TestUtils.saveAndFlush(product);

        product = new SUSEProduct();
        product.setName("suse-manager-prov-single");
        product.setVersion("1.2");
        product.setFriendlyName("SUSE Manager Prov Single 1.2");
        product.setProductId(1097);
        TestUtils.saveAndFlush(product);
    }
}
