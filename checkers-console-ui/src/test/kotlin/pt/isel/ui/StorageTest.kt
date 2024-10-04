package pt.isel

import storage.FileStorage
import storage.StringSerializer
import java.io.File
import kotlin.test.*

class StorageTest {
    class DummyEntity(
        val id: Int,
        val address: String? = null
    )
    object DummySerializer : StringSerializer<DummyEntity> {
        override fun write(obj: DummyEntity) = "${obj.id};${obj.address}"
        override fun parse(input: String): DummyEntity {
            val words = input.split(";")
            return DummyEntity(words[0].toInt(), words[1])
        }
    }

    val testFolder = "Output"
    val dummyId = 123

    @BeforeTest fun setup() {
        val f = File("$testFolder/$dummyId.txt")
        if(f.exists()) f.delete()
    }

    @Test fun `Create an entity with existing id throws exception`() {
        val fs = FileStorage(testFolder, DummySerializer, ::DummyEntity)
        fs.new(dummyId)
        val err = assertFailsWith<IllegalArgumentException> { fs.new(dummyId) }
        assertEquals(err.message, "There is already an entity with given id $dummyId")
    }

    @Test fun `Load an entity with unknown id returns null`() {
        val fs = FileStorage(testFolder, DummySerializer, ::DummyEntity)
        assertNull(fs.load(7612541))
    }

    @Test fun `Load an existing entity`() {
        val fs = FileStorage(testFolder, DummySerializer, ::DummyEntity)
        fs.new(dummyId)
        assertNotNull(fs.load(dummyId))
    }

    @Test fun `Save an entity and load it`() {
        val fs = FileStorage(testFolder, DummySerializer, ::DummyEntity)
        fs.new(dummyId)
        fs.save(dummyId, DummyEntity(dummyId, "Rua Rosa"))
        val obj = fs.load(dummyId)
        assertNotNull(obj)
        assertEquals(dummyId, obj.id)
        assertEquals("Rua Rosa", obj.address)
    }
}