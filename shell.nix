{ pkgs ? import <nixpkgs> {} }:
    pkgs.mkShell {
        packages = with pkgs;
        [
            jetbrains.idea-community-bin
        ];

        nativeBuildInputs = with pkgs.buildPackages;
        [
            jdk22
            maven
        ];
        
}
