/**
 * Copyright (c) 2016 SUSE LLC
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
package com.suse.manager.webui.utils.test;

import com.suse.manager.webui.controllers.MinionsAPI;
import com.suse.manager.webui.controllers.utils.RegularMinionBootstrapper;
import com.suse.manager.webui.utils.InputValidator;
import com.suse.manager.webui.utils.gson.BootstrapHostsJson;
import com.suse.manager.webui.utils.gson.BootstrapParameters;

import java.util.List;

import junit.framework.TestCase;

/**
 * Tests for the InputValidator.
 */
public class InputValidatorTest extends TestCase {

    private static final String HOST_ERROR_MESSAGE = "Invalid host name.";
    private static final String USER_ERROR_MESSAGE = "Non-valid user. Allowed characters" +
            " are: letters, numbers, '.', '\\', '-' and '_'";
    private static final String PORT_ERROR_MESSAGE = "Port must be a number within range" +
            " 1-65535.";

    /**
     * Test the check for required fields.
     */
    public void testValidateBootstrapInputUserEmpty() {
        String json = "{user: ''}";
        BootstrapHostsJson input = MinionsAPI.GSON.fromJson(json, BootstrapHostsJson.class);
        BootstrapParameters params = RegularMinionBootstrapper.getInstance().createBootstrapParams(input);
        List<String> validationErrors = InputValidator.INSTANCE.validateBootstrapInput(params);
        assertTrue(validationErrors.size() == 2);
        assertTrue(validationErrors.contains(HOST_ERROR_MESSAGE));
        assertTrue(validationErrors.contains(USER_ERROR_MESSAGE));
    }

    /**
     * Test the check for user with letters and numbers.
     */
    public void testValidateBootstrapInputUserLettersNumbers() {
        String json = "{user: 'Admin1', host: 'host.domain.com'}";
        BootstrapHostsJson input = MinionsAPI.GSON.fromJson(json, BootstrapHostsJson.class);
        BootstrapParameters params = RegularMinionBootstrapper.getInstance().createBootstrapParams(input);
        List<String> validationErrors = InputValidator.INSTANCE.validateBootstrapInput(params);
        assertTrue(validationErrors.isEmpty());
    }

    /**
     * Test the check for user with dot.
     */
    public void testValidateBootstrapInputUserDot() {
        String json = "{user: 'my.admin', host: 'host.domain.com'}";
        BootstrapHostsJson input = MinionsAPI.GSON.fromJson(json, BootstrapHostsJson.class);
        BootstrapParameters params = RegularMinionBootstrapper.getInstance().createBootstrapParams(input);
        List<String> validationErrors = InputValidator.INSTANCE.validateBootstrapInput(params);
        assertTrue(validationErrors.isEmpty());
    }

    /**
     * Test the check for user with backslash.
     */
    public void testValidateBootstrapInputUserBackslash() {
        String json = "{user: 'domain\\\\admin', host: 'host.domain.com'}";
        BootstrapHostsJson input = MinionsAPI.GSON.fromJson(json, BootstrapHostsJson.class);
        BootstrapParameters params = RegularMinionBootstrapper.getInstance().createBootstrapParams(input);
        List<String> validationErrors = InputValidator.INSTANCE.validateBootstrapInput(params);
        assertTrue(validationErrors.isEmpty());
    }

    /**
     * Test the check for user with dash.
     */
    public void testValidateBootstrapInputUserDash() {
        String json = "{user: 'my-admin', host: 'host.domain.com'}";
        BootstrapHostsJson input = MinionsAPI.GSON.fromJson(json, BootstrapHostsJson.class);
        BootstrapParameters params = RegularMinionBootstrapper.getInstance().createBootstrapParams(input);
        List<String> validationErrors = InputValidator.INSTANCE.validateBootstrapInput(params);
        assertTrue(validationErrors.isEmpty());
    }

    /**
     * Test the check for user with underscore.
     */
    public void testValidateBootstrapInputUserUnderscore() {
        String json = "{user: 'my_admin', host: 'host.domain.com'}";
        BootstrapHostsJson input = MinionsAPI.GSON.fromJson(json, BootstrapHostsJson.class);
        BootstrapParameters params = RegularMinionBootstrapper.getInstance().createBootstrapParams(input);
        List<String> validationErrors = InputValidator.INSTANCE.validateBootstrapInput(params);
        assertTrue(validationErrors.isEmpty());
    }

    /**
     * Test the check for user with invalid character.
     */
    public void testValidateBootstrapInputUserInvalid() {
        String json = "{user: '$(execme)', host: 'host.domain.com'}";
        BootstrapHostsJson input = MinionsAPI.GSON.fromJson(json, BootstrapHostsJson.class);
        BootstrapParameters params = RegularMinionBootstrapper.getInstance().createBootstrapParams(input);
        List<String> validationErrors = InputValidator.INSTANCE.validateBootstrapInput(params);
        assertTrue(validationErrors.size() == 1);
        assertTrue(validationErrors.contains(USER_ERROR_MESSAGE));
    }

    /**
     * Test the check for host with invalid character.
     */
    public void testValidateBootstrapInputHostInvalid() {
        String json = "{user: 'toor', host: '`execme`'}";
        BootstrapHostsJson input = MinionsAPI.GSON.fromJson(json, BootstrapHostsJson.class);
        BootstrapParameters params = RegularMinionBootstrapper.getInstance().createBootstrapParams(input);
        List<String> validationErrors = InputValidator.INSTANCE.validateBootstrapInput(params);
        assertTrue(validationErrors.size() == 1);
        assertTrue(validationErrors.contains(HOST_ERROR_MESSAGE));
    }

    /**
     * Test the check for host as an IPv4.
     */
    public void testValidateBootstrapInputHostIPv4() {
        String json = "{user: 'toor', host: '192.168.1.1'}";
        BootstrapHostsJson input = MinionsAPI.GSON.fromJson(json, BootstrapHostsJson.class);
        BootstrapParameters params = RegularMinionBootstrapper.getInstance().createBootstrapParams(input);
        List<String> validationErrors = InputValidator.INSTANCE.validateBootstrapInput(params);
        assertTrue(validationErrors.isEmpty());
    }

    /**
     * Test the check for host as an IPv6.
     */
    public void testValidateBootstrapInputHostIPv6() {
        String json = "{user: 'toor', host: '[2001:0db8:0000:0000:0000:0000:1428:57ab]'}";
        BootstrapHostsJson input = MinionsAPI.GSON.fromJson(json, BootstrapHostsJson.class);
        BootstrapParameters params = RegularMinionBootstrapper.getInstance().createBootstrapParams(input);
        List<String> validationErrors = InputValidator.INSTANCE.validateBootstrapInput(params);
        assertTrue(validationErrors.isEmpty());
    }

    /**
     * Test the check for required fields, user is "root" per default.
     */
    public void testValidateBootstrapInputDefaultUser() {
        String json = "{}";
        BootstrapHostsJson input = MinionsAPI.GSON.fromJson(json, BootstrapHostsJson.class);
        BootstrapParameters params = RegularMinionBootstrapper.getInstance().createBootstrapParams(input);
        List<String> validationErrors = InputValidator.INSTANCE.validateBootstrapInput(params);
        assertTrue(validationErrors.size() == 1);
        assertTrue(validationErrors.contains(HOST_ERROR_MESSAGE));
    }

    /**
     * Test minimal input (only host and user).
     */
    public void testValidateBootstrapInputMinimal() {
        String json = "{host: 'host.domain.com', user: 'root'}";
        BootstrapHostsJson input = MinionsAPI.GSON.fromJson(json, BootstrapHostsJson.class);
        BootstrapParameters params = RegularMinionBootstrapper.getInstance().createBootstrapParams(input);
        List<String> validationErrors = InputValidator.INSTANCE.validateBootstrapInput(params);
        assertTrue(validationErrors.isEmpty());
    }

    /**
     * Test the check for empty port numbers.
     */
    public void testValidateBootstrapInputPortEmpty() {
        String json = "{host: 'host.domain.com', user: 'root', port: ''}";
        BootstrapHostsJson input = MinionsAPI.GSON.fromJson(json, BootstrapHostsJson.class);
        BootstrapParameters params = RegularMinionBootstrapper.getInstance().createBootstrapParams(input);
        List<String> validationErrors = InputValidator.INSTANCE.validateBootstrapInput(params);
        assertTrue(validationErrors.isEmpty());
    }

    /**
     * Test the check for port numbers outside the valid range.
     */
    public void testValidateBootstrapInputPortRange() {
        String json = "{host: 'host.domain.com', user: 'root', port: '99999'}";
        BootstrapHostsJson input = MinionsAPI.GSON.fromJson(json, BootstrapHostsJson.class);
        BootstrapParameters params = RegularMinionBootstrapper.getInstance().createBootstrapParams(input);
        List<String> validationErrors = InputValidator.INSTANCE.validateBootstrapInput(params);
        assertTrue(validationErrors.size() == 1);
        assertTrue(validationErrors.contains(PORT_ERROR_MESSAGE));

        json = "{host: 'host.domain.com', user: 'root', port: '-1'}";
        input = MinionsAPI.GSON.fromJson(json, BootstrapHostsJson.class);
        params = RegularMinionBootstrapper.getInstance().createBootstrapParams(input);
        validationErrors = InputValidator.INSTANCE.validateBootstrapInput(params);
        assertTrue(validationErrors.size() == 1);
        assertTrue(validationErrors.contains(PORT_ERROR_MESSAGE));
    }

    /**
     * Verify that valid port numbers validate.
     */
    public void testValidateBootstrapInputPortValid() {
        String json = "{host: 'host.domain.com', user: 'root', port: '8888'}";
        BootstrapHostsJson input = MinionsAPI.GSON.fromJson(json, BootstrapHostsJson.class);
        BootstrapParameters params = RegularMinionBootstrapper.getInstance().createBootstrapParams(input);
        List<String> validationErrors = InputValidator.INSTANCE.validateBootstrapInput(params);
        assertTrue(validationErrors.isEmpty());
    }
}
