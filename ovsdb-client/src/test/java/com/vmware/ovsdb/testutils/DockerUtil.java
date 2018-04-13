/*
 * Copyright (c) 2018 VMware, Inc. All Rights Reserved.
 *
 * This product is licensed to you under the BSD-2 license (the "License").
 * You may not use this product except in compliance with the BSD-2 License.
 *
 * This product may include a number of subcomponents with separate copyright
 * notices and license terms. Your use of these subcomponents is subject to the
 * terms and conditions of the subcomponent's license, as noted in the LICENSE
 * file.
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */

package com.vmware.ovsdb.testutils;

import com.google.common.collect.ImmutableMap;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.ExecCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DockerUtil {

  private static DockerClient dockerClient;

  static  {
    try {
      dockerClient = DefaultDockerClient.fromEnv().build();
    } catch (DockerCertificateException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Start a docker container.
   *
   * @param image the name of the docker image to instantiate
   * @param cmd the entry point command after starting the container
   * @return the container id
   */
  public static String startContainer(String image, String cmd)
      throws DockerException, InterruptedException {
    final ContainerConfig containerConfig = ContainerConfig.builder()
        .image(image)
        .cmd("sh", "-c", cmd).build();

    final ContainerCreation creation = dockerClient.createContainer(containerConfig);
    final String id = creation.id();

    dockerClient.startContainer(id);
    return id;
  }

  /**
   * Start n containers with the same image and same entry point command.
   *
   * @param image the name of the docker image to instantiate
   * @param cmd the entry point command after starting the container
   * @return the container id
   */
  public static List<String> startContainers(
      int count, String image, String cmd
  ) throws DockerException, InterruptedException {
    List<String> containerIds = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      String id = startContainer(image, cmd);
      containerIds.add(id);
    }
    return containerIds;
  }

  /**
   * Start a container with a port binding.
   * All the traffic to the port on the container will be forwarded to the same port on the host.
   *
   * @param image the name of the docker image to instantiate
   * @param port the port to bind to the host
   * @param cmd the entry point command after starting the container
   * @return the container id
   */
  public static String startContainerWithPortBinding(String image, String port, String cmd)
      throws DockerException, InterruptedException {
    final List<PortBinding> hostPorts = Collections.singletonList(PortBinding.of("0.0.0.0", port));
    final Map<String, List<PortBinding>> portBindings = ImmutableMap.of(port, hostPorts);

    final HostConfig hostConfig = HostConfig.builder().portBindings(portBindings).build();

    final ContainerConfig containerConfig = ContainerConfig.builder()
        .hostConfig(hostConfig)
        .image(image).exposedPorts(port)
        .cmd("sh", "-c", cmd).build();

    final ContainerCreation creation = dockerClient.createContainer(containerConfig);
    final String id = creation.id();

    dockerClient.startContainer(id);
    return id;
  }

  /**
   * Stop and remove all the containers.
   *
   * @param ids ids of the containers to kill.
   */
  public static void killContainers(List<String> ids) throws DockerException, InterruptedException {
    for (String id : ids) {
      dockerClient.killContainer(id);
      dockerClient.removeContainer(id);
    }
  }

  /**
   * Execute a command on a container.
   *
   * @param id id of the container
   * @param command the command to execute
   * @return the output of the command
   */
  public static String execCommand(String id, String command)
      throws DockerException, InterruptedException {
    final String[] cmd = {"sh", "-c", command};
    final ExecCreation execCreation = dockerClient.execCreate(
        id, cmd, DockerClient.ExecCreateParam.attachStdout(),
        DockerClient.ExecCreateParam.attachStderr());
    final LogStream output = dockerClient.execStart(execCreation.id());
    return output.readFully();
  }
}
