package us.gibb.dev.vo_gen;

import java.io.File;

import static org.junit.Assert.*;
import org.junit.Test;

public class TestVOGen {

    @Test
    public void testGenerate() {
        try {
            VOGen gen = new VOGen();
            gen.setDefaultPackage("us.gibb.dev.vo_gen.model.vo");
            gen.setOutDir(new File("target/testgen"));
            gen.setPackages(new String[]{"us.gibb.dev.vo_gen.model"});
            gen.setSrcDir(new File("src/test/java"));
            gen.setTestOption("testing123");
            gen.generate();
        } catch (Exception e) {
            e.printStackTrace();
            fail("unexpected exception");
        }
        File voPkg = new File("target/testgen/us/gibb/dev/vo_gen/model/vo");
        assertTrue(voPkg.exists());
        File vo = new File(voPkg, "UserVO.java");
        assertTrue(vo.exists());
        File converter = new File(voPkg, "VO.java");
        assertTrue(converter.exists());
    }
}
