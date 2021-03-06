/*
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package org.apache.hadoop.hive.ql.exec.repl.bootstrap;

import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.exec.repl.bootstrap.events.DatabaseEvent;
import org.apache.hadoop.hive.ql.exec.repl.bootstrap.events.filesystem.BootstrapEventsIterator;
import org.apache.hadoop.hive.ql.exec.repl.bootstrap.events.filesystem.ConstraintEventsIterator;
import org.apache.hadoop.hive.ql.plan.Explain;

import java.io.IOException;
import java.io.Serializable;

@Explain(displayName = "Replication Load Operator", explainLevels = { Explain.Level.USER,
    Explain.Level.DEFAULT,
    Explain.Level.EXTENDED })
public class ReplLoadWork implements Serializable {
  final String dbNameToLoadIn;
  final String tableNameToLoadIn;
  private final BootstrapEventsIterator iterator;
  private final ConstraintEventsIterator constraintsIterator;
  private int loadTaskRunCount = 0;
  private DatabaseEvent.State state = null;

  public ReplLoadWork(HiveConf hiveConf, String dumpDirectory, String dbNameToLoadIn,
      String tableNameToLoadIn) throws IOException {
    this.tableNameToLoadIn = tableNameToLoadIn;
    this.iterator = new BootstrapEventsIterator(dumpDirectory, dbNameToLoadIn, hiveConf);
    this.constraintsIterator = new ConstraintEventsIterator(dumpDirectory, hiveConf);
    this.dbNameToLoadIn = dbNameToLoadIn;
  }

  public ReplLoadWork(HiveConf hiveConf, String dumpDirectory, String dbNameOrPattern)
      throws IOException {
    this(hiveConf, dumpDirectory, dbNameOrPattern, null);
  }

  public BootstrapEventsIterator iterator() {
    return iterator;
  }

  public ConstraintEventsIterator constraintIterator() {
    return constraintsIterator;
  }

  int executedLoadTask() {
    return ++loadTaskRunCount;
  }

  void updateDbEventState(DatabaseEvent.State state) {
    this.state = state;
  }

  DatabaseEvent databaseEvent(HiveConf hiveConf) {
    DatabaseEvent databaseEvent = state.toEvent(hiveConf);
    return databaseEvent;
  }

  boolean hasDbState() {
    return state != null;
  }
}
