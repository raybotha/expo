export default [
  {
    name: 'simulator',
    enum: 'boolean',
    description: [ 'If set to true, creates build for simulator. Defaults to false' ],
  },
  {
    name: 'enterpriseProvisioning',
    enum: [ 'universal', 'adhoc' ],
    description: [ 'Provisioning method used for `"distribution": "internal"` when you have an Apple account with Apple Developer Enterprise Program membership. You can choose if you want to use `adhoc` or `universal` provisioning. The latter is recommended as it does not require you to register each individual device. If you don\'t provide this option and you still authenticate with an enterprise team, you\'ll be prompted which provisioning method to use.',
    ]
  },
  {
    name: 'autoIncrement',
    type: 'boolean | \"version\" | \"buildNumber\"',
    description: [
      'Controls how EAS CLI bumps your application build version. Defaults to `false`',
      ' - `"version"` - the patch of `expo.version` is bumped (e.g. `1.2.3` -> `1.2.4`).',
      ' - `"buildNumber"` (or `true`) - the last component of `expo.devs.buildNumber` is bumped (e.g. `1.2.3.39` -> `1.2.3.40`).',
      ' - `false` - versions won\'t be bumped automatically',
      '',
      'In the case of a bare project, it also updates versions in native code. `expo.version` corresponds to `CFBundleShortVersionString` and `expo.devs.buildNumber` to `CFBundleVersion` in the `Info.plist`. The App Store is using those values to identify the app build, `CFBundleShortVersionString` is the version visible to users, whereas `CFBundleVersion` defines the build number. The combination of those needs to be unique, so you can bump either of them.',
    ],
  },
  {
    name: 'image',
    type: 'string',
    description: [
      'Image with build environment. [Learn more about it here](../../build-reference/infrastructure).',
    ],
  },
  {
    name: 'bundler',
    type: 'string',
    description: [ 'Version of [bundler](https://bundler.io/).' ],
  },
  {
    name: 'fastlane',
    type: 'string',
    description: [ 'Version of fastlane.' ],
  },
  {
    name: 'cocoapods',
    type: 'string',
    description: [ 'Version of CocoaPods.' ],
  },
  {
    name: 'scheme',
    type: 'string',
    description: [
      'Xcode project\'s scheme.',
      ' - managed project: does not have any effect',
      ' - bare project',
      '   - If your project has multiple schemes, you should set this value.',
      '   - If the project has only one scheme, it will be detected automatically.',
      '   - If multiple schemes exist and this value is **not** set, EAS CLI will prompt you to select one of them.',

    ]
  },
  {
    name: 'buildConfiguration',
    type: 'string',
    description: [
      'Xcode project\'s Build Configuration.',
      ' - managed project: "Release" or "Debug", defaults to "Release"',
      ' - bare project: defaults to the value specified in the scheme',
      '',
      'It takes priority over `developmentClient` field.',
    ],
  },
  {
    name: 'artifactPath',
    type: 'string',
    description: [
      'Path (or pattern) where EAS Build is going to look for the build artifacts. EAS Build uses the `fast-glob` npm package for pattern matching, ([see their README to learn more about the syntax you can use](https://github.com/mrmlnc/fast-glob#pattern-syntax)). You should modify that path only if you are using a custom `Gymfile`. The default is `ios/build/Build/Products/*-iphonesimulator/*.app` when building for simulator and `ios/build/*.ipa` in other cases.'
    ],
  },
]
