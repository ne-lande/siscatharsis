{ pkgs ? import <nixpkgs> {} }:
    pkgs.mkShell {
        nativeBuildInputs = with pkgs.buildPackages;
        [
            jdk22
            maven
        ];
}
