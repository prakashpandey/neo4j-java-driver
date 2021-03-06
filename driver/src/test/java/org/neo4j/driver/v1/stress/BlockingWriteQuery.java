/*
 * Copyright (c) 2002-2018 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.neo4j.driver.v1.stress;

import org.neo4j.driver.v1.AccessMode;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

import static org.junit.Assert.assertEquals;

public class BlockingWriteQuery<C extends AbstractContext> extends AbstractBlockingQuery<C>
{
    private AbstractStressTestBase<C> stressTest;

    public BlockingWriteQuery( AbstractStressTestBase<C> stressTest, Driver driver, boolean useBookmark )
    {
        super( driver, useBookmark );
        this.stressTest = stressTest;
    }

    @Override
    public void execute( C context )
    {
        StatementResult result = null;
        Throwable queryError = null;

        try ( Session session = newSession( AccessMode.WRITE, context ) )
        {
            result = session.run( "CREATE ()" );
        }
        catch ( Throwable error )
        {
            queryError = error;
            if ( !stressTest.handleWriteFailure( error, context ) )
            {
                throw error;
            }
        }

        if ( queryError == null && result != null )
        {
            assertEquals( 1, result.summary().counters().nodesCreated() );
            context.nodeCreated();
        }
    }
}
