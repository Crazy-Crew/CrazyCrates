### CrazyCrates GitHub Flow
In order to ensure the stability of my sanity when handling this repository, and version tabbing. I am writing an essay for my future self.

**You must not UNDER any circumstance commit to the main branch**, (unless you are **ryderbelserion**) All changes should be made via a pull request.

## Release Builds
* You should only *version bump* when you are *ready* to release.
* The commit when version bumping should always be separated, and clearly labeled like `v0.0.1 Update`
* `ryderbelserion` is the only person who does releases.

## Beta Builds
* All pull requests should be made to the `dev` branch.
* All work should be done in a properly labeled branch.
    * If you find you are fixing a bug, label the branch as beta-fix/bug_desc
    * If you find you are adding a feature, label the branch as beta-feat/feat_desc and so on.

## Alpha Builds
* `alpha` branch is meant for changes that are not for production servers!
* If you would like contribute, The work you do should be done in a properly labeled branch based on `alpha` branch.
    * If you find you are fixing a bug, label the branch as alpha-fix/bug_desc
    * If you find you are adding a feature, label the branch as alpha-feat/feat_desc and so on.
* The build workflow for `alpha` branches is triggered manually.