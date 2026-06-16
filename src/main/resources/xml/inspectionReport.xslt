<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="html" indent="yes" encoding="UTF-8"
                doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
                doctype-system="http://www.w3.org/TR/html4/loose.dtd"/>

    <xsl:template match="/">
        <html>
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
                <title>Отчёт инспектора</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    h1 { color: #2c3e50; }
                    table { border-collapse: collapse; width: 100%; margin-top: 20px; }
                    th, td { border: 1px solid #95a5a6; padding: 10px; text-align: left; }
                    th { background-color: #ecf0f1; font-weight: bold; }
                    .total td { font-weight: bold; background-color: #f9f9f9; }
                </style>
            </head>
            <body>
                <h1>Отчёт инспектора</h1>

                <!-- Информация о декларации -->
                <p><strong>Номер декларации:</strong> <xsl:value-of select="//declarationId"/></p>
                <p><strong>Декларант:</strong> <xsl:value-of select="//declarant"/></p>

                <table>
                    <thead>
                        <tr>
                            <th>Код товара (ТН ВЭД)</th>
                            <th>Наименование</th>
                            <th>Количество</th>
                            <th>Таможенная стоимость</th>
                            <th>Пошлина</th>
                        </tr>
                    </thead>
                    <tbody>
                        <xsl:for-each select="//item">
                            <tr>
                                <td><xsl:value-of select="code"/></td>
                                <td><xsl:value-of select="name"/></td>
                                <td><xsl:value-of select="quantity"/></td>
                                <td><xsl:value-of select="format-number(customsValue, '#,##0.00')"/></td>
                                <td><xsl:value-of select="calculatedDuty"/></td>
                            </tr>
                        </xsl:for-each>
                    </tbody>
                    <tfoot>
                        <tr class="total">
                            <td colspan="4">Итого пошлин к уплате:</td>
                            <td><xsl:value-of select="format-number(sum(//item/calculatedDuty), '#,##0.00')"/></td>
                        </tr>
                    </tfoot>
                </table>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>