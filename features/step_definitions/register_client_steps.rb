# Copyright (c) 2010-2017 SUSE-LINUX
# Licensed under the terms of the MIT license.
Given(/^I update the profile of this client$/) do
  $client.run("rhn-profile-sync", true, 500, 'root')
end

When(/^I register using "([^"]*)" key$/) do |arg1|
  regurl = "http://#{$server_ip}/XMLRPC"
  command = "rhnreg_ks --force --serverUrl=#{regurl} --activationkey=#{arg1}"
  $client.run(command, true, 500, 'root')
end

When(/^I register using an activation key$/) do
  arch, _code = $client.run('uname -m')
  arch.chomp!
  step %(I register using "1-SUSE-DEV-#{arch}" key)
end

Then(/^I should see this client in spacewalk$/) do
  steps %(
    Given I am on the Systems page
    Then I should see this client as link
    )
end

Then(/^I should see this client as link$/) do
  step %(I should see a "#{$client_fullhostname}" link)
end

Then(/^config-actions are enabled$/) do
  unless file_exists?($client, '/etc/sysconfig/rhn/allowed-actions/configfiles/all')
    raise "config actions are disabled: /etc/sysconfig/rhn/allowed-actions/configfiles/all does not exist on client"
  end
end

Then(/^remote-commands are enabled$/) do
  unless file_exists?($client, '/etc/sysconfig/rhn/allowed-actions/script/run')
    raise "remote-commands are disabled: /etc/sysconfig/rhn/allowed-actions/script/run does not exist"
  end
end

When(/^I wait for the data update$/) do
  for c in 0..6
    if page.has_content?(debrand_string("Software Updates Available"))
      break
    end
    sleep 30
    visit current_url
  end
end
