/*
 * ProActive Parallel Suite(TM):
 * The Open Source library for parallel and distributed
 * Workflows & Scheduling, Orchestration, Cloud Automation
 * and Big Data Analysis on Enterprise Grids & Clouds.
 *
 * Copyright (c) 2007 - 2017 ActiveEon
 * Contact: contact@activeeon.com
 *
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation: version 3 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 */
package functionaltests.service;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.ow2.proactive.scheduler.common.JobDescriptor;
import org.ow2.proactive.scheduler.common.SchedulerEvent;
import org.ow2.proactive.scheduler.common.TaskDescriptor;
import org.ow2.proactive.scheduler.common.exception.UnknownJobException;
import org.ow2.proactive.scheduler.common.exception.UnknownTaskException;
import org.ow2.proactive.scheduler.common.job.JobId;
import org.ow2.proactive.scheduler.common.job.TaskFlowJob;
import org.ow2.proactive.scheduler.common.task.JavaTask;
import org.ow2.proactive.scheduler.common.task.TaskId;
import org.ow2.proactive.scheduler.descriptor.EligibleTaskDescriptor;
import org.ow2.proactive.scheduler.descriptor.JobDescriptorImpl;
import org.ow2.proactive.scheduler.job.JobIdImpl;
import org.ow2.proactive.scheduler.task.TaskResultImpl;


public class SchedulingServiceTest4 extends BaseServiceTest {

    private TaskFlowJob createTestJob() throws Exception {
        TaskFlowJob job = new TaskFlowJob();
        job.setName(this.getClass().getSimpleName());

        JavaTask task1 = new JavaTask();
        task1.setName("javaTask");
        task1.setExecutableClassName("class");
        task1.setMaxNumberOfExecution(3);
        job.addTask(task1);

        return job;
    }

    @Test
    public void testTaskRestart1() throws Exception {
        service.submitJob(createJob(createTestJob()));
        listener.assertEvents(SchedulerEvent.JOB_SUBMITTED);

        JobDescriptor jobDesc = startTask();

        try {
            service.restartTask(jobDesc.getJobId(), "invalid task name", 100);
            Assert.fail();
        } catch (UnknownTaskException e) {
        }
        try {
            service.restartTask(JobIdImpl.makeJobId("1234567"), "javaTask", 100);
            Assert.fail();
        } catch (UnknownJobException e) {
        }

        service.restartTask(jobDesc.getJobId(), "javaTask", 100);

        listener.assertEvents(SchedulerEvent.JOB_PENDING_TO_RUNNING,
                              SchedulerEvent.JOB_UPDATED,
                              SchedulerEvent.TASK_PENDING_TO_RUNNING,
                              SchedulerEvent.TASK_WAITING_FOR_RESTART);
        infrastructure.assertRequests(1);

        startTask();

        TaskId taskId = ((JobDescriptorImpl) jobDesc).getInternal().getTask("javaTask").getId();
        service.taskTerminatedWithResult(taskId, new TaskResultImpl(taskId, "OK", null, 0));

        listener.assertEvents(SchedulerEvent.TASK_PENDING_TO_RUNNING,
                              SchedulerEvent.TASK_RUNNING_TO_FINISHED,
                              SchedulerEvent.JOB_RUNNING_TO_FINISHED,
                              SchedulerEvent.JOB_UPDATED);
        infrastructure.assertRequests(1);
    }

    @Test
    public void testTaskRestart2() throws Exception {
        service.submitJob(createJob(createTestJob()));
        listener.assertEvents(SchedulerEvent.JOB_SUBMITTED);

        JobDescriptor jobDesc;

        jobDesc = startTask();

        service.restartTask(jobDesc.getJobId(), "javaTask", 100);
        listener.assertEvents(SchedulerEvent.JOB_PENDING_TO_RUNNING,
                              SchedulerEvent.JOB_UPDATED,
                              SchedulerEvent.TASK_PENDING_TO_RUNNING,
                              SchedulerEvent.TASK_WAITING_FOR_RESTART);
        infrastructure.assertRequests(1);

        startTask();
        service.restartTask(jobDesc.getJobId(), "javaTask", 100);
        listener.assertEvents(SchedulerEvent.TASK_PENDING_TO_RUNNING, SchedulerEvent.TASK_WAITING_FOR_RESTART);
        infrastructure.assertRequests(1);

        startTask();
        service.restartTask(jobDesc.getJobId(), "javaTask", 100);
        listener.assertEvents(SchedulerEvent.TASK_PENDING_TO_RUNNING,
                              SchedulerEvent.TASK_RUNNING_TO_FINISHED,
                              SchedulerEvent.JOB_RUNNING_TO_FINISHED,
                              SchedulerEvent.JOB_UPDATED);
        infrastructure.assertRequests(1);
    }

    private JobDescriptor startTask() throws Exception {
        Map<JobId, JobDescriptor> jobsMap;
        JobDescriptor jobDesc;

        jobsMap = service.lockJobsToSchedule();
        assertEquals(1, jobsMap.size());
        jobDesc = jobsMap.values().iterator().next();
        Assert.assertEquals(1, jobDesc.getEligibleTasks().size());
        for (TaskDescriptor taskDesc : jobDesc.getEligibleTasks()) {
            taskStarted(jobDesc, (EligibleTaskDescriptor) taskDesc);
        }
        service.unlockJobsToSchedule(jobsMap.values());

        return jobDesc;
    }
}
