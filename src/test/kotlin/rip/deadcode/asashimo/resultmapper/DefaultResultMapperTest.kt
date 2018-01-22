package rip.deadcode.asashimo.resultmapper

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import rip.deadcode.asashimo.AsashimoConfig
import java.sql.ResultSet
import java.sql.ResultSetMetaData

class DefaultResultMapperTest {

    @Test
    fun testBasicType1() {
        val rs = mock(ResultSet::class.java)
        `when`(rs.getInt(1)).thenReturn(123)
        val res = convertToBasicType(Int::class, rs, AsashimoConfig())

        assertThat(res).isEqualTo(123)
    }

    @Test
    fun testBasicType2() {
        val rs = mock(ResultSet::class.java)
        `when`(rs.getBytes(1)).thenReturn(byteArrayOf(62, 63, 64))
        val res = convertToBasicType(ByteArray::class, rs, AsashimoConfig())

        assertThat(res).isEqualTo(byteArrayOf(62, 63, 64))
    }

    private data class User1(val id: Int, val name: String)

    @Test
    fun testAllArgConstructor1() {
        val rs = mock(ResultSet::class.java)
        val meta = mock(ResultSetMetaData::class.java)
        `when`(meta.columnCount).thenReturn(2)
        `when`(rs.metaData).thenReturn(meta)
        `when`(rs.getInt(1)).thenReturn(123)
        `when`(rs.getString(2)).thenReturn("John")
        val res = ConstructorResultMapper.convertWithAllArgsConstructor(User1::class, rs, AsashimoConfig())!!

        assertThat(res.id).isEqualTo(123)
        assertThat(res.name).isEqualTo("John")
    }

    private class User2(var id: Int = 0, var name: String = "")

    @Test
    fun testBean1() {
        val rs = mock(ResultSet::class.java)
        val meta = mock(ResultSetMetaData::class.java)
        `when`(meta.columnCount).thenReturn(2)
        `when`(meta.getColumnName(1)).thenReturn("ID")
        `when`(meta.getColumnName(2)).thenReturn("NAME")
        `when`(rs.metaData).thenReturn(meta)
        `when`(rs.getObject("ID", Int::class.java)).thenReturn(123)
        `when`(rs.getObject("NAME", String::class.java)).thenReturn("John")
        val res = BeanResultMapper.convertAsBean(User2::class, rs)!!

        assertThat(res.id).isEqualTo(123)
        assertThat(res.name).isEqualTo("John")
    }

    private data class User3(val id: Int, val name: String?)

    @Test
    fun testAllArgConstructor2() {
        val rs = mock(ResultSet::class.java)
        val meta = mock(ResultSetMetaData::class.java)
        `when`(meta.columnCount).thenReturn(2)
        `when`(rs.metaData).thenReturn(meta)
        `when`(rs.getInt(1)).thenReturn(123)
        `when`(rs.getString(2)).thenReturn(null)
        val res = ConstructorResultMapper.convertWithAllArgsConstructor(User3::class, rs, AsashimoConfig())!!

        assertThat(res.id).isEqualTo(123)
        assertThat(res.name).isNull()
    }

}