package imadz.team.efficiency.domain.service.git

object GitCommands {
  val conf_remote_origin_url = "remote.origin.url"

  def git_config_get(dir: String, key: String): String = s"${common_prefix(dir)} config --get $key"

  def common_prefix(dir: String) = s"git --git-dir=${dir}/.git --work-tree=${dir}"

  def log_full_history_pretty_fuller(dir: String): String = s"${common_prefix(dir)} log --full-history --pretty=fuller"

  def cat_file_p(dir: String)(commitId: String) = s"${common_prefix(dir)} cat-file -p ${commitId}"

  def diff_tree_full_index_r_M_C(dir: String)(commitId: String) = s"${common_prefix(dir)} diff-tree --full-index -r -M -C -B"

  def git_reset_hard_and_update(workdir: String, branch: String): List[String] =
    git_checkout(workdir, branch) :: git_rebase(workdir) :: git_reset_hard_head(workdir) :: git_pull(workdir) :: Nil

  def git_checkout(workdir: String, branch: String): String = s"${common_prefix(workdir)} checkout $branch "

  def git_rebase(workdir: String): String =
    s"${common_prefix(workdir)} rebase "

  def git_reset_hard_head(workdir: String): String =
    s"${common_prefix(workdir)} reset --hard HEAD~"

  def git_pull(workdir: String): String =
    s"${common_prefix(workdir)} pull"

  def git_clone(repoDirectory: String, repositoryUrl: String, branch: String): String = {
    s"git clone --branch $branch $repositoryUrl $repoDirectory"
  }

}
