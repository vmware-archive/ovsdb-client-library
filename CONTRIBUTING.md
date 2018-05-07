

# Contributing to ovsdb-client-library

The ovsdb-client-library project team welcomes contributions from the community. Before you start working with ovsdb-client-library, please read our [Developer Certificate of Origin](https://cla.vmware.com/dco). All contributions to this repository must be signed as described on that page. Your signature certifies that you wrote the patch or have the right to pass it on as an open-source patch.

## Contribution Flow

This is a rough outline of what a contributor's workflow looks like:

- Create a topic branch from where you want to base your work
- Make commits of logical units
- Make sure your commit messages are in the proper format (see below)
- Push your changes to a topic branch in your fork of the repository
- Submit a pull request

Example:

``` shell
git remote add upstream https://github.com/vmware/ovsdb-client-library.git
git checkout -b my-new-feature master
git commit -a
git push origin my-new-feature
```

### Staying In Sync With Upstream

When your branch gets out of sync with the vmware/master branch, use the following to update:

``` shell
git checkout my-new-feature
git fetch -a
git pull --rebase upstream master
git push --force-with-lease origin my-new-feature
```

### Updating pull requests

If your PR fails to pass CI or needs changes based on code review, you'll most likely want to squash these changes into
existing commits.

If your pull request contains a single commit or your changes are related to the most recent commit, you can simply
amend the commit.

``` shell
git add .
git commit --amend
git push --force-with-lease origin my-new-feature
```

If you need to squash changes into an earlier commit, you can use:

``` shell
git add .
git commit --fixup <commit>
git rebase -i --autosquash master
git push --force-with-lease origin my-new-feature
```

Be sure to add a comment to the PR indicating your new changes are ready to review, as GitHub does not generate a
notification when you git push.

### Code Style
Google Java style is used: [intellij-java-google-style](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml)
or [eclipse-java-google-style](https://github.com/google/styleguide/blob/gh-pages/eclipse-java-google-style.xml). checkstyle plugin is used with google_checks.xml.

### Formatting Commit Messages

We follow the conventions on [How to Write a Git Commit Message](http://chris.beams.io/posts/git-commit/).

Be sure to include any related GitHub issue references in the commit message.  See
[GFM syntax](https://guides.github.com/features/mastering-markdown/#GitHub-flavored-markdown) for referencing issues
and commits.

## Reporting Bugs and Creating Issues

Please report any issues [here](https://github.com/vmware/ovsdb-client-library/issues). When opening a new issue, try to roughly follow the commit message format conventions above.

## Repository Structure
```
ovsdb-client-library
 |
 +---README.md
 |
 +---pom.xml
 |
 +---json-rpc
 |      |
 |      +---pom.xml
 |      |
 |      +---src
 |
 +---ovsdb-client
 |      |
 |      +---pom.xml
 |      |
 |      +---src
```
## How to Run Tests
### Prerequisites
* Maven 3+
* Docker (For integration test only)

### Unit Tests
Under root directory, run:
```bash
$ mvn clean test
```

### Integration Tests
Make sure you have docker service installed. Then run the following command:
```bash
$ mvn clean verify -Dhost.ip=<host-ip>
```
For Mac OS, `<host-ip>` is the IP of your host. For Linux, `<host-ip>` is the IP of the `docker0` interface.
