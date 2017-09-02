<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:param name="Name" />
    <xsl:template match="/">
        <html>
            <body>
                <h1>Groups for project <xsl:value-of select="$Name"/></h1>
                <table border="1">
                    <tr>
                        <th>GroupName</th>
                        <th>State</th>
                    </tr>
                    <xsl:for-each select="/*[name()='Payload']/*[name()='Projects']/*[name()='Project']">
                        <xsl:if test="./@identifier = $Name">
                            <xsl:for-each select="./*[name()='Group']">
                                <tr>
                                    <td>
                                        <xsl:value-of select="./*[name()='fullName']" />
                                    </td>
                                    <td>
                                        <xsl:value-of select="./@state" />
                                    </td>
                                </tr>
                            </xsl:for-each>
                        </xsl:if>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>