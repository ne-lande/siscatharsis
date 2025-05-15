{
inputs.nixpkgs.url = "nixpkgs/nixpkgs-unstable";
outputs = inputs:
let
  system = "x86_64-linux";
  pkgs = inputs.nixpkgs.legacyPackages.${system};
in {
  devShell.${system} = pkgs.mkShell rec {
    name = "java-shell";
    buildInputs = with pkgs; [
        jdk21_headless
        maven
        zsh
    ];

    shellHook = ''
      export JAVA_HOME=${pkgs.jdk21_headless}
      PATH="${pkgs.jdk21_headless}/bin:$PATH"

      if [[ $- == *i* ]]; then
        exec ${pkgs.zsh}/bin/zsh
      fi
    '';
  };
 };
}