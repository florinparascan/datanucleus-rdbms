/**********************************************************************
Copyright (c) 2008 Andy Jefferson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors:
    ...
**********************************************************************/
package org.datanucleus.store.rdbms.sql.method;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.sql.expression.NumericExpression;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.store.rdbms.sql.expression.StringLiteral;
import org.datanucleus.store.rdbms.sql.expression.TemporalExpression;
import org.datanucleus.util.Localiser;

/**
 * Method for evaluating {dateExpr}.getMinute() using MSSQL.
 * Returns a NumericExpression that equates to <pre>DATEPART(mi, CAST(expr AS 'DATETIME'))</pre>
 */
public class DateGetMinute4Method extends AbstractSQLMethod
{
    /* (non-Javadoc)
     * @see org.datanucleus.store.rdbms.sql.method.SQLMethod#getExpression(org.datanucleus.store.rdbms.sql.expression.SQLExpression, java.util.List)
     */
    public SQLExpression getExpression(SQLExpression expr, List args)
    {
        if (!(expr instanceof TemporalExpression))
        {
            throw new NucleusException(Localiser.msg("060001", "getMinute()", expr));
        }

        RDBMSStoreManager storeMgr = stmt.getRDBMSManager();
        JavaTypeMapping mapping = storeMgr.getMappingManager().getMapping(String.class);
        SQLExpression mi = exprFactory.newLiteral(stmt, mapping, "mi");
        ((StringLiteral)mi).generateStatementWithoutQuotes();

        ArrayList funcArgs = new ArrayList();
        funcArgs.add(mi);

        // CAST {invokedExpr} AS DATETIME
        List castArgs = new ArrayList<>();
        castArgs.add(expr);

        funcArgs.add(new TemporalExpression(stmt, getMappingForClass(Date.class), "CAST", castArgs, asList("DATETIME")));
        return new NumericExpression(stmt, getMappingForClass(int.class), "DATEPART", funcArgs);
    }
}