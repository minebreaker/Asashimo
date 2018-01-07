package rip.deadcode.asashimo

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import java.sql.Connection
import java.sql.PreparedStatement

class StatementGeneratorTest {

    @Test
    fun testLex1() {
        val result = StatementGenerator.lex("select id, name from user;")
        assertThat(result).isEqualTo(listOf("select", "id", ",", "name", "from", "user", ";"))
    }

    @Test
    fun testLex2() {
        val result = StatementGenerator.lex("""
select
    id
    , name
from
    user
;
            """)
        assertThat(result).isEqualTo(listOf("select", "id", ",", "name", "from", "user", ";"))
    }

    @Test
    fun testLex3() {
        val result = StatementGenerator.lex("""
                select
                    id, name, ranking
                from user
                left join user_rank
                on user.id = user_rank.id
                where
                    user.name = "?John?"
                    and user.password = ?
                """.trimIndent())
        assertThat(result).isEqualTo(listOf(
                "select", "id", ",", "name", ",", "ranking", "from", "user", "left", "join", "user_rank", "on",
                "user.id", "=", "user_rank.id", "where", "user.name", "=", "\"?John?\"", "and", "user.password", "=",
                "?"))
    }

    @Test
    fun testLex4() {
        val result = StatementGenerator.lex("' ? foo ? bar ? ';")
        assertThat(result).isEqualTo(listOf("' ? foo ? bar ? '", ";"))
    }

    @Test
    fun test1() {
        val conn = mock(Connection::class.java)
        val stmt = mock(PreparedStatement::class.java)
        `when`(conn.prepareStatement(any())).thenReturn(stmt)

        val result = StatementGenerator.create(
                conn,
                "select id, name from user where id = :id and password = :name",
                mapOf("id" to 123, "name" to "Robert'); DROP TABLE Students;--"))

        assertThat(result === stmt).isTrue()

        verify(conn).prepareStatement("select id , name from user where id = ? and password = ?")
        verify(stmt).setInt(1, 123)
        verify(stmt).setString(2, "Robert'); DROP TABLE Students;--")
    }

    @Test(expected = IllegalStateException::class)
    fun test2() {
        val conn = mock(Connection::class.java)
        StatementGenerator.create(conn, "select * from user where id = ?", params = mapOf("id" to 123))
    }

}