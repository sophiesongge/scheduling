/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.ow2.proactive.scheduler.common;

import java.io.Serializable;

import org.objectweb.proactive.annotation.PublicAPI;
import org.ow2.proactive.scheduler.common.job.Job;
import org.ow2.proactive.scheduler.common.job.JobInfo;
import org.ow2.proactive.scheduler.common.job.UserIdentification;
import org.ow2.proactive.scheduler.common.task.TaskInfo;


/**
 * Class providing events that the scheduler is able to send using the described listener.
 *
 * @author The ProActive Team
 * @since ProActive Scheduling 0.9
 *
 * @param <E> The job is used in the event that can either be an {@link InternalJob} for administrator or {@link Job} for a user.
 */
@PublicAPI
public interface SchedulerEventListener extends Serializable {

    /**
     * Invoked when the scheduler has just been started.
     */
    public void schedulerStartedEvent();

    /**
     * Invoked when the scheduler has just been stopped.
     */
    public void schedulerStoppedEvent();

    /**
     * Invoked when the scheduler has just been paused.
     *
     */
    public void schedulerPausedEvent();

    /**
     * Invoked when the scheduler has received a freeze signal.
     */
    public void schedulerFrozenEvent();

    /**
     * Invoked when the scheduler has just been resumed.
     */
    public void schedulerResumedEvent();

    /**
     * Invoked when the scheduler shutdown sequence is initialized.
     */
    public void schedulerShuttingDownEvent();

    /**
     * Invoked when the scheduler has just been shutdown.
     */
    public void schedulerShutDownEvent();

    /**
     * Invoked when the scheduler has just been killed.<br>
     *
     * Scheduler is not reachable anymore.
     */
    public void schedulerKilledEvent();

    /**
     * Invoked when a job has been paused on the scheduler.
     *
     * @param info the informations on the paused job.
     */
    public void jobPausedEvent(JobInfo info);

    /**
     * Invoked when a job has been resumed on the scheduler.
     *
     * @param info the informations on the resumed job.
     */
    public void jobResumedEvent(JobInfo info);

    /**
     * Invoked when the scheduler has received a new job to schedule.
     *
     * @param job the new job to schedule.
     */
    public void jobSubmittedEvent(Job job);

    /**
     * Invoked when the scheduling of a job has just started.<br>
     * The description of the job is contained in the given jobInfo.<br>
     * Use Job.update(JobInfo) to update your job.
     *
     * @param info the event describing the job concerned.
     */
    public void jobPendingToRunningEvent(JobInfo info);

    /**
     * Invoked when the scheduling of a job has just been terminated.<br>
     * The description of the job is contained in the given jobInfo.<br>
     * Use {@link Job}.update(JobInfo) to update your job.
     *
     * @param info the event describing the job concerned.
     */
    public void jobRunningToFinishedEvent(JobInfo info);

    /**
     * Invoked when the scheduler has removed a job due to result reclamation.<br>
     * The description of the job is contained in the given jobInfo.<br>
     * Use {@link Job}.update(JobInfo) to update your job.
     *
     * @param info the event describing the job concerned.
     */
    public void jobRemoveFinishedEvent(JobInfo info);

    /**
     * Invoked when the scheduling of a task has just started.<br>
     * The description of the task is contained in the TaskInfo given.<br>
     * Use {@link Job}.update(TaskInfo) to update your job.
     *
     * @param info the event describing the task concerned.
     */
    public void taskPendingToRunningEvent(TaskInfo info);

    /**
     * Invoked when the scheduling of a task has just finished.<br>
     * The description of the task is contained in the TaskInfo given.<br>
     * Use {@link Job}.update(TaskInfo) to update your job.
     *
     * @param info the event describing the task concerned.
     */
    public void taskRunningToFinishedEvent(TaskInfo info);

    /**
     * Invoked when a task had an error (error code or exception).
     * The task will be restart after a dynamic amount of time.
     * This event specified that a task is waiting for restart.
     *
     * @param info the event describing the task concerned.
     */
    public void taskWaitingForRestart(TaskInfo info);

    /**
     * Invoked when the scheduler has changed the priority of a job.<br>
     * The description of the job is contained in the given jobInfo.<br>
     * Use {@link Job}.update(JobInfo) to update your job.
     *
     * @param info the event describing the job concerned.
     */
    public void jobChangePriorityEvent(JobInfo info);

    /**
     * Invoked if the Resource Manager has failed.<br>
     * Use the {@link AdminSchedulerInterface#linkResourceManager(String rmURL)} to reconnect a new Resource Manager.
     */
    public void schedulerRMDownEvent();

    /**
     * Invoked when the Resource Manager has been reconnect to the scheduler.
     */
    public void schedulerRMUpEvent();

    /**
     * Invoked when a new user is connected or when a user submit a new job.
     *
     * @param userIdentification the identification of the user that have just connect/disconnect the scheduler.
     */
    public void usersUpdate(UserIdentification userIdentification);

    /**
     * Invoked when the scheduling policy has been changed.
     *
     * @param newPolicyName the new name of the policy as a string.
     */
    public void schedulerPolicyChangedEvent(String newPolicyName);

}
