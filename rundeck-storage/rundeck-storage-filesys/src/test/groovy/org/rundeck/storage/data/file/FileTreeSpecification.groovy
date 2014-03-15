package org.rundeck.storage.data.file

import org.rundeck.storage.data.DataUtil
import spock.lang.Specification

/**
 * $INTERFACE is ...
 * User: greg
 * Date: 2/18/14
 * Time: 11:27 AM
 */
class FileTreeSpecification extends Specification {
    File testDir
    def setup(){
        testDir=new File("build/filetree-tests")
        testDir.mkdirs()
    }
    def cleanup(){
        testDir.delete()
    }
    def "basic constructor creates dirs"() {
        def dir = new File(testDir,"root1")
        def ft = FileTreeUtil.forRoot(dir,DataUtil.contentFactory())
        expect:
        dir.exists()
        dir.isDirectory()
        new File(dir,"meta").exists()
        new File(dir,"meta").isDirectory()
        new File(dir,"content").exists()
        new File(dir,"content").isDirectory()
    }
    def "store basic resource"(){
        def dir = new File(testDir, "root1")
        def contentdir = new File(dir,"content")
        def metadir = new File(testDir, "root1/meta")
        def ft = FileTreeUtil.forRoot(dir, DataUtil.contentFactory())
        def expectedDataFile = new File(contentdir, "test/a/bc/mydata.txt")
        def expectedMetaFile = new File(metadir, "test/a/bc/mydata.txt")
        expectedDataFile.deleteOnExit()
        expectedMetaFile.deleteOnExit()
        when:
        def resource=ft.createResource("test/a/bc/mydata.txt",
                DataUtil.withText("sample text",[monkey:'blister','Content-Type':'text/plain'],DataUtil.contentFactory()))
        then:
        resource.path.path=='test/a/bc/mydata.txt'
        resource.contents.meta.monkey=='blister'
        resource.contents.meta['Content-Type']=='text/plain'

        expectedDataFile.exists()
        expectedDataFile.isFile()
        expectedDataFile.text=='sample text'

        expectedMetaFile.exists()
        expectedMetaFile.isFile()
        expectedMetaFile.text=='{"monkey":"blister","Content-Type":"text/plain"}'
    }
}
