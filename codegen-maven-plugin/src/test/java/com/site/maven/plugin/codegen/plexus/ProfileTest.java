package com.site.maven.plugin.codegen.plexus;

import org.junit.Assert;
import org.junit.Test;

import com.site.helper.Files;
import com.site.maven.plugins.plexus.profile.entity.Profile;
import com.site.maven.plugins.plexus.profile.transform.DefaultDomParser;

public class ProfileTest {
   @Test
   public void testXml() throws Exception {
      String expected = Files.forIO().readFrom(getClass().getResourceAsStream("profile.xml"), "utf-8");
      Profile profile = new DefaultDomParser().parse(expected);

      Assert.assertEquals("XML is not well parsed!", expected.replace("\r", ""), profile.toString().replace("\r", ""));
   }
}
