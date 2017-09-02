<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        <html>
            <body>
                <h1>Users</h1>
                <table border="1">
                    <tr>
                        <th>Email</th>
                        <th>Name</th>
                    </tr>
                    <xsl:for-each select="/*[name()='Payload']/*[name()='Users']/*[name()='User']">
                        <tr>

                            <td>
                                <xsl:value-of select="./*[name()='fullName']" />
                            </td>
                            <td>
                                <xsl:value-of select="./@email" />
                            </td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
